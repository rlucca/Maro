#
# Makefile para compilar o documento exemplo da classe LaTeX para a UNISINOS.
#


# Nome do arquivo principal .tex, sem a extensão.
MAIN	= lucca
FILES   = .OAlock		 \
		  $(MAIN).tex    \
          resumos.tex    \
          cronograma.tex \
          estadoArte.tex \
          introducao.tex \
          trabalho.tex	 \
          conclusao.tex  \
          FonteOntologia.tex  \
          mestrado.bib   \
		  Makefile
ontoDir = onto/


# Regra simplista para gerar um PostScript do documento.
all: $(MAIN).pdf
	@-echo "feito."

$(MAIN).pdf: $(FILES)
	pdflatex $(MAIN) && bibtex $(MAIN) && pdflatex $(MAIN) && pdflatex $(MAIN)

.OAlock: $(ontoDir)
	@echo "ontology changed but dissertation no!" && exit 5


# Remoção dos arquivos temporários (lista não exaustiva).
clean:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz *.tdo *.ent

mrproper:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz lucca.pdf *.dvi *.tdo *.ent

view: all
	open $(MAIN).pdf
