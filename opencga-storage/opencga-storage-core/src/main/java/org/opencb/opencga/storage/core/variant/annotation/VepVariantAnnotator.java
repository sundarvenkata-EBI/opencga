package org.opencb.opencga.storage.core.variant.annotation;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencb.biodata.formats.annotation.io.VepFormatReader;
import org.opencb.biodata.models.variant.annotation.VariantAnnotation;
import org.opencb.cellbase.core.client.CellBaseClient;
import org.opencb.cellbase.core.lib.DBAdaptorFactory;
import org.opencb.cellbase.core.lib.api.variation.VariantAnnotationDBAdaptor;
import org.opencb.cellbase.core.lib.api.variation.VariationDBAdaptor;
import org.opencb.datastore.core.QueryOptions;
import org.opencb.opencga.storage.core.variant.adaptors.VariantDBAdaptor;
import org.opencb.opencga.storage.core.variant.io.json.VariantAnnotationMixin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.opencb.opencga.lib.common.ExceptionUtils.getExceptionString;

/**
 * Created by fjlopez on 10/04/15.
 */
public class VepVariantAnnotator implements VariantAnnotator {
    private final JsonFactory factory;
    private ObjectMapper jsonObjectMapper;

    protected static Logger logger = LoggerFactory.getLogger(VepVariantAnnotator.class);

    public VepVariantAnnotator() {
        this.factory = new JsonFactory();
        this.jsonObjectMapper = new ObjectMapper(factory);
        jsonObjectMapper.addMixInAnnotations(VariantAnnotation.class, VariantAnnotationMixin.class);
    }

    public static VepVariantAnnotator buildVepAnnotator() {
            return new VepVariantAnnotator();
    }

    private static void checkNull(String value, String name) throws VariantAnnotatorException {
        if(value == null || value.isEmpty()) {
            throw new VariantAnnotatorException("Missing value: " + name);
        }
    }

    /////// CREATE ANNOTATION: empty. Vep annotation must be created beforehand by using VEP's cli and stored in a vep format file

    @Override
    public URI createAnnotation(VariantDBAdaptor variantDBAdaptor, Path outDir, String fileName, QueryOptions options) {
        return null;
    }

    /////// LOAD ANNOTATION

    @Override
    public void loadAnnotation(final VariantDBAdaptor variantDBAdaptor, final URI uri, QueryOptions options) throws IOException {

        final int batchSize = options.getInt(VariantAnnotationManager.BATCH_SIZE, 100);
        final int numConsumers = options.getInt(VariantAnnotationManager.NUM_WRITERS, 6);
        final int numProducers = 1;
        ExecutorService executor = Executors.newFixedThreadPool(numConsumers + numProducers);
        final BlockingQueue<VariantAnnotation> queue = new ArrayBlockingQueue<>(batchSize*numConsumers*2);
        final VariantAnnotation lastElement = new VariantAnnotation();

        executor.execute(new Runnable() {   // producer
            @Override
            public void run() {
                try {
                    int annotationsCounter = 0;

                    /** Open vep reader **/
                    VepFormatReader vepFormatReader = new VepFormatReader(Paths.get(uri).toFile().toString());
                    vepFormatReader.open();
                    vepFormatReader.pre();

                    /** Read annotations **/
                    List<VariantAnnotation> variantAnnotation;
                    while ((variantAnnotation = vepFormatReader.read()) != null) {
                        queue.put(variantAnnotation.get(0));  // read() method always returns a list of just one element
                        annotationsCounter++;
                        if (annotationsCounter % 1000 == 0) {
                            logger.info("Element {}", annotationsCounter);
                        }
                    }
                    for (int i = 0; i < numConsumers; i++) {    //Add a lastElement marker. Consumers will stop reading when read this element.
                        queue.put(lastElement);
                    }
                    logger.debug("Put Last element. queue size = {}", queue.size());
                    vepFormatReader.post();
                    vepFormatReader.close();
                } catch (InterruptedException e) {
                    logger.error(getExceptionString(e));
                } catch (Exception e) {
                    logger.error(getExceptionString(e));
                    logger.error("Thread ends UNEXPECTEDLY due to an exception raising.");
                    try {
                        for (int i = 0; i < numConsumers; i++) {    //Add a lastElement marker. Consumers will stop reading when read this element.
                            queue.put(lastElement);
                        }
                        logger.debug("Consumers were notified to finish.");
                    } catch (InterruptedException ie) {
                        logger.error("Another exception occurred when finishing.");
                        logger.error(getExceptionString(e));
                    }
                }
            }
        });

        for (int i = 0; i < numConsumers; i++) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<VariantAnnotation> batch = new ArrayList<>(batchSize);
                        VariantAnnotation elem = queue.take();
                        while (elem != lastElement) {
                            batch.add(elem);
                            if (batch.size() == batchSize) {
                                variantDBAdaptor.updateAnnotations(batch, new QueryOptions());
                                batch.clear();
                                logger.debug("thread updated batch");
                            }
                            elem = queue.take();
                        }
                        if (!batch.isEmpty()) { //Upload remaining elements
                            variantDBAdaptor.updateAnnotations(batch, new QueryOptions());
                        }
                        logger.debug("thread finished updating annotations");
                    } catch (InterruptedException e) {
                        logger.error(getExceptionString(e));
                    } catch (Exception e) {
                        logger.error(getExceptionString(e));
                        logger.error("Thread ends UNEXPECTEDLY due to exception raising.");
                    }
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Integer.MAX_VALUE, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("annotation interrupted");
            logger.error(getExceptionString(e));
        } catch (Exception e) {
            logger.error(getExceptionString(e));
            logger.error("Thread executor ends UNEXPECTEDLY due to exception raising.");
            executor.shutdown();
        }

        /** Join
         try {
         producerThread.join();
         for (Thread consumerThread : consumers) {
         consumerThread.join();
         }
         } catch (InterruptedException e) {
         e.printStackTrace();
         }
         **/
//        while (parser.nextToken() != null) {
//            VariantAnnotation variantAnnotation = parser.readValueAs(VariantAnnotation.class);
////            System.out.println("variantAnnotation = " + variantAnnotation);
//            batch.add(variantAnnotation);
//            if(batch.size() == batchSize || parser.nextToken() == null) {
//                variantDBAdaptor.updateAnnotations(batch, new QueryOptions());
//                batch.clear();
//            }
//        }

    }

}
