# Overview
OpenCGA is an open-source project that aims to provide a Big Data storage engine and analysis framework for genomic scale data analysis of hundreds of terabytes or even petabytes. OpenCGA provides a scalable and high-performance **Storage Engine** framework to index biological data such as BAM or VCF files using different NoSQL databases, currently only MongoDB has been fully developed. A data analytics and genomic **Analysis** layer interface has been implemented over this big data storage index. A metadata **Catalog** has been alse developed to provide authentification and ACLs and to keep track all of files and sample annotation. All these can be queried through a comprehensive RESTful web services API or using the command line interface.

OpenCGA constitutes the big data analysis component of [OpenCB](http://www.opencb.org/) initiative. It is used by other projects such as [EMBL-EBI EVA](http://www.ebi.ac.uk/eva/), [Babelomics](http://www.babelomics.org/) or [BierApp](http://bierapp.babelomics.org/).

### Documentation
You can find OpenCGA documentation and tutorials at: https://github.com/opencb/opencga/wiki.

### Issues Tracking
You can report bugs or request new features at [GitHub issue tracking](https://github.com/opencb/opencga/issues).

### Release Notes and Roadmap
Releases notes are available at [GitHub releases](https://github.com/opencb/opencga/releases).

Roadmap is available at [GitHub milestones](https://github.com/opencb/opencga/milestones). You can report bugs or request new features at [GitHub issue tracking](https://github.com/opencb/opencga/issues).

### Versioning
OpenCGA is versioned following the rules from [Semantic versioning](http://semver.org/).

### Maintainers
We recommend to contact OpenCGA developers by writing to OpenCB mailing list opencb@googlegroups.com. The main developers and maintainers are:
* Ignacio Medina (im411@cam.ac.uk) (_Founder and Project Leader_)
* Cristina Y. Gonzalez (cyenyxe@ebi.ac.uk)
* Jacobo Coll (jcoll@ebi.ac.uk)
* Jose M. Mut (jmmut@ebi.ac.uk)

##### Other Contributors
* Matthias Haimel (mh719@cam.ac.uk)
* Roberto Alonso (ralonso@cipf.es)
* Alejandro Aleman (aaleman@cipf.es)
* Franscisco Salavert (fsalavert@cipf.es)

##### Contributing
OpenCGA is an open-source and collaborative project. We appreciate any help and feeback from users, you can contribute in many different ways such as simple bug reporting and feature request. Dependending on your skills you are more than welcome to develop client tools, new features or even fixing bugs.


# How to build 
OpenCGA is mainly developed in Java and it uses [Apache Maven](http://maven.apache.org/) as build tool. OpenCGA requires Java 7+ and a set of other OpenCB Java dependencies that can be found in [Maven Central Repository](http://search.maven.org/).

Stable releases are merged and tagged at **_master_** branch, you are encourage to use latest stable release for production. Current active development is carried out at **_develop_** branch, only compilation is guaranteed and bugs are expected, use this branch for development or for testing new functionalities. Only dependencies of **_master_** branch are ensured to be deployed at [Maven Central Repository](http://search.maven.org/), **_develop_** branch may require users to download and install other active OpenCB repositories:
* _biodata_: https://github.com/opencb/biodata (branch 'develop')
* _datastore_: https://github.com/opencb/datastore (branch 'develop')
* _cellbase_: https://github.com/opencb/cellbase (branch 'develop')

### Cloning
OpenCGA is an open-source and free project, you can download default **_develop_** branch by executing:

    imedina@ivory:~$ git clone https://github.com/opencb/opencga.git
    Cloning into 'opencga'...
    remote: Counting objects: 20267, done.
    remote: Compressing objects: 100% (219/219), done.
    remote: Total 20267 (delta 105), reused 229 (delta 35)
    Receiving objects: 100% (20267/20267), 7.23 MiB | 944.00 KiB/s, done.
    Resolving deltas: 100% (6363/6363), done.

Latest stable release at **_master_** branch can be downloaded executing:

    imedina@ivory:~$ git clone -b master https://github.com/opencb/opencga.git
    Cloning into 'opencga'...
    remote: Counting objects: 20267, done.
    remote: Compressing objects: 100% (219/219), done.
    remote: Total 20267 (delta 105), reused 229 (delta 35)
    Receiving objects: 100% (20267/20267), 7.23 MiB | 812.00 KiB/s, done.
    Resolving deltas: 100% (6363/6363), done.


### Build
You can build OpenCGA by executing the following command from the root of the cloned repository:
  
    $ mvn clean install -DskipTests

Remember that **_develop_** branch dependencies are not ensured to be deployed at Maven Central, you may need to clone and install **_develop_** branches from OpenCB _biodata_, _datastore_ and _cellbase_ repositories. After this you should have this file structure in **_opencga-app/build_**:

    opencga-app/build/
    ├── analysis
    ├── bin
    ├── conf
    └── libs

You can copy the content of the _build_ folder into any directory such as _/opt/opencga_.

### Testing
You can run the unit tests using Maven or your favorite IDE. Just notice that some tests may require of certain database back-ends such as MongoDB or Apache HBase and may fail if they are not available.

### Command Line Interface (CLI)
If the build process has gone well you should get an integrated help by executing:

    ./bin/opencga.sh --help

You can find more detailed documentation and tutorials at: https://github.com/opencb/opencga/wiki.

### Other Dependencies
We try to improve the user experience by making the installation and build as simple as possible. Unfortunately, for some OpenCGA components and functionalities other dependencies are required.

##### Loading data
At this moment the only fully developed storage engine plugin is [MongoDB](https://www.mongodb.org/). MongoDB is free and open-source and can be downloaded from [here](https://www.mongodb.org/downloads). Currently Apache HBase plugin is under heavy development and will be ready soon.

##### AES encryption
For AES encryption please download UnlimitedJCEPolicyJDK7.zip from http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html .
Then unzip the file into $JAVA_HOME/jre/lib/security
