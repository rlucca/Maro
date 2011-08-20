#
# Makefile para compilar o documento exemplo da classe LaTeX para a UNISINOS.
#


# Nome do arquivo principal .tex, sem a extensão.
MAIN	= lucca


# Regra simplista para gerar um PostScript do documento.
all: $(MAIN).pdf
	@-echo "feito."

$(MAIN).pdf: $(MAIN).tex
	pdflatex $(MAIN) && bibtex $(MAIN) && pdflatex $(MAIN) && pdflatex $(MAIN)

# Remoção dos arquivos temporários (lista não exaustiva).
clean:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz *.tdo

mrproper:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz lucca.pdf *.dvi *.tdo

view: $(MAIN).pdf
	open $(MAIN).pdf
