#
# Makefile para compilar o documento exemplo da classe LaTeX para a UNISINOS.
#


# Nome do arquivo principal .tex, sem a extensão.
MAIN	= lucca
FILES   = 	$(MAIN).tex \
			abreviaturas.tex \
			agradecimentos.tex \
			dedicatoria.tex \
			introducao.tex \
			resumo.tex \
			metodologia.tex \
			levantamento.tex \
			desenvolvimento.tex \
			caso.tex \
			conclusao.tex \
			FonteJason.tex \
			FonteExemploRoom.tex \
			FonteExemploGameLife.tex \
			../Mistery.bib

# Regra simplista para gerar um PostScript do documento.
all: $(MAIN).pdf
	@-echo "feito."

$(MAIN).pdf: $(FILES) mestrado.bib
	pdflatex $(MAIN) && bibtex $(MAIN) && pdflatex $(MAIN) && pdflatex $(MAIN)

mestrado.bib: ../Mistery.bib
	@-cp -i ../Mistery.bib mestrado.bib

# Remoção dos arquivos temporários (lista não exaustiva).
clean:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz *.tdo mestrado.bib

mrproper:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz lucca.pdf *.dvi *.tdo mestrado.bib

view: $(MAIN).pdf
	open $(MAIN).pdf
