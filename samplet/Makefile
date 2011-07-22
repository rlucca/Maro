upath=$(shell pwd)
# NAO GERE O JAR, ELE NAO EH NECESSARIO PARA RODAR
# O APLICATIVO ENTAO SO NAO GERE-O, OKAY??

all: run
	@-echo feito.

compile:
	ant -f $(upath)/bin/build.xml compile

clean:
	ant -f $(upath)/bin/build.xml clean

run: compile
	ant -f $(upath)/bin/build.xml

