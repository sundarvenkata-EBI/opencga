package org.opencb.opencga.storage.core.variant.io.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.opencb.biodata.models.variant.stats.VariantHardyWeinbergStats;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public abstract class VariantStatsJsonMixin {
    
    @JsonIgnore public abstract String[] getAltAlleles();
    
    @JsonIgnore public abstract boolean isIndel();
          
    @JsonIgnore public abstract boolean isSNP();
    
    @JsonIgnore public abstract String getId();

    @JsonIgnore public abstract VariantHardyWeinbergStats getHw();

    @JsonIgnore public abstract boolean isTransition();

    @JsonIgnore public abstract boolean isTransversion();
}
