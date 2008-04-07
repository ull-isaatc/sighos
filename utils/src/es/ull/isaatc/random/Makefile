# Makefile for Java Programs Vers 1.0

# Class Path, where it should load classes from, needs your directory
# and probably the System class directory also
CLASSPATH = ..:.:/usr/local/java/classes/

# Compiler can be javac or javac_g for debuggers like jdb
JAVAC = /usr/java/bin/javac 
JAVADOC = /usr/java/bin/javadoc

# JAVA Compiler Flags
JFLAGS = -g

KERNEL= RandomNumber.java\
	Uniform.java Exponential.java Normal.java\
	Geometric.java Poisson.java Triangular.java\
	Erlang.java ChiSquare.java Beta.java\
	Tally.java WeightedTally.java TimeSeriesTally.java 

JAVA_SOURCES = $(KERNEL) 

JAVA_OBJS =  ${JAVA_SOURCES:.java=.class}

# Default rule for compiling Java programs on the SGI
.SUFFIXES: 	.java .class
.java.class: 
	$(JAVAC) $(JFLAGS) $<

default: $(JAVA_OBJS)
	@echo " Done Compiling $(JAVA_SOURCES) into $(JAVA_OBJS)\n";

clean: 
	rm *.class *.html;

tar:
	tar cvf Utility.tar *.java Makefile 

allDocs:
	$(JAVADOC) -d ../htmlDocs Utility SimKit 
	
