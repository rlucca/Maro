#
# Makefile para compilar o documento exemplo da classe LaTeX para a UNISINOS.
#


# Nome do arquivo principal .tex, sem a extensão.
MAIN	= lucca
PREFIX  = ../../..
FILES   = 	$(MAIN).tex 				\
			$(PREFIX)/Mistery.bib		\
			conclusao.tex 				\
			introducao.tex 				\
			casoDeUso.tex 				\
			levantamento.tex 			\
			proposto.tex 				\
			plataformaJason.tex			\
			programacaoAgentes.tex		\
			computacaoAfetiva.tex		\
			abreviaturas.tex 			\
			assinaturas.tex 			\
			conceitosAgente.tex			\
			paradigmaAOP.tex			\
			resumo.tex

.sampleConsoleLock: ../../sampletConsole
	@echo "dissertation not changed, but samplet console yes" && exit 5

# Regra simplista para gerar um PostScript do documento.
all: $(MAIN).pdf
	@-echo "feito."

$(MAIN).pdf: .sampleConsoleLock $(FILES) mestrado.bib
	pdflatex $(MAIN) && bibtex $(MAIN) && pdflatex $(MAIN) && pdflatex $(MAIN)

mestrado.bib: $(PREFIX)/Mistery.bib
	@-cp -i $(PREFIX)/Mistery.bib mestrado.bib

# Remoção dos arquivos temporários (lista não exaustiva).
clean:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz *.tdo

mrproper:
	rm -fv *.aux *.toc *.lo[lagft] *.bbl *.blg *.out *.nav *.snm *.ps *.synctex.gz lucca.pdf *.dvi *.tdo mestrado.bib

view: $(MAIN).pdf
	open $(MAIN).pdf
