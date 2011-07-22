upath=$(shell pwd)

all:
	@-echo feito.

jar:
	ant -f $(upath)/bin/build.xml jar

compile: jar
	ant -f $(upath)/example/game-of-life/bin/build.xml compile
	ant -f $(upath)/example/gold-miners/bin/build.xml compile

clean:
	ant -f $(upath)/bin/build.xml clean
	rm $(upath)/maro.jar
	#ant -f $(upath)/example/game-of-life/bin/build.xml clean
	#ant -f $(upath)/example/gold-miners/bin/build.xml clean

gol: compile
	ant -f $(upath)/example/game-of-life/bin/build.xml

gm: compile
	ant -f $(upath)/example/gold-miners/bin/build.xml

run:
	ant -f $(upath)/bin/build.xml
