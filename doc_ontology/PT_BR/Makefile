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
          trabalho.tex	 \
          conclusao.tex  \
		  Makefile
affectiveOnto = ../../ontology/occ-tbox.owl


# Regra simplista para gerar um PostScript do documento.
all: $(MAIN).pdf
	@-echo "feito."

$(MAIN).pdf: $(FILES)
	pdflatex $(MAIN) && bibtex $(MAIN) && pdflatex $(MAIN) && pdflatex $(MAIN)

.OAlock: $(affectiveOnto)
	@echo "affective ontology changed but dissertation no!" && exit 5


# Remoção dos arquivos temporários (lista não exaustiva).
clean:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz *.tdo

mrproper:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz lucca.pdf *.dvi *.tdo

view: all
	open $(MAIN).pdf
