LIBS=`ls lib/* | xargs | tr ' ' ':'`:src
#Nao pode fazer isso, coloque a infrastrutura em .jason/user.properties
#INFRA=-Dinfrastructure.XXX=maro.wrapper.AffectiveFactory
PROJECT=soccer
ANT=ant -f bin/build.xml

compile: bin/build.xml
	@-$(ANT) compile

bin/build.xml:
	#@javac -cp $(LIBS) src/maro/wrapper/AffectiveFactory.java
	@-java -cp $(LIBS) jason.mas2j.parser.mas2j $(PROJECT).mas2j
	@[ -f bin/build.xml ]
	#@-find . -name \*.class -exec rm {} \;

clean:
	@-rm -rf bin
	#@-find . -name \*.class -exec rm {} \;

run: bin/build.xml
	@time $(ANT) run

# Nao serve para executar sem colocar o manifest correto no xml
jar: bin/build.xml
	@-$(ANT) jar

