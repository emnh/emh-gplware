# Introductions and manuals #
  * [On Wikibooks](http://en.wikibooks.org/wiki/LaTeX)
  * [Nice introductory presentation](http://docs.google.com/viewer?a=v&q=cache:yTnbff_vUaoJ:www.cs.uc.edu/~rangana/Professional/includes/presentation_09_workshop_latex.pdf+algorithm2e+displaymath&hl=en&gl=no&pid=bl&srcid=ADGEESilsELVyuS8D8c3p_2R_IKBIZES3J4CRSKAVDwjGFkwsXYDfmTh55fjHp4wNQf4D-PEVy_1CTd0i8PrCIQuzGHzCQ0V479Ygt1krbbyCrYUCZV2d_Z8qHFmAAf_3ukG6YCLdAml&sig=AHIEtbRdPGJyIl4Q-cd_oKTTMOBDQoHeaA)
  * [Not so short introduction to LaTeX](https://docs.google.com/viewer?url=http://www.ctan.org/tex-archive/info/lshort/english/lshort.pdf)
  * [LaTeX FAQ](https://docs.google.com/viewer?url=http://www.tex.ac.uk/tex-archive/help/uk-tex-faq/newfaq.pdf)
  * [Latex2e Reference Manual](http://www.informatik.uni-hamburg.de/RZ/software/tex/info/latex2e-help-texinfo/latex2e.html)
  * [LaTeX best practices (StackOverflow)](http://stackoverflow.com/questions/193298/best-practices-in-latex)
  * [Is LaTeX worth learning today (StackOverflow)](http://stackoverflow.com/questions/874576/is-latex-worth-learning-today)

# Mathematics #

  * [Mathematics (Wikibooks)](http://en.wikibooks.org/wiki/LaTeX/Mathematics)
  * [Advanced Mathematics (Wikibooks)](http://en.wikibooks.org/wiki/LaTeX/Advanced_Mathematics)

# Build system #

  * I'm using [latex-makefile](http://code.google.com/p/latex-makefile/).
  * [rubber](http://www.pps.jussieu.fr/~beffara/soft/rubber/) also seems good. Nicer to extend since it's written in Python.
  * [Other options](http://stackoverflow.com/questions/1240037/recommended-build-system-for-latex)

# Incremental viewing #

I use `gv -watch test.pdf` and just overwrite the file when a new working version has been produced by the build system.

# Cool packages #

## Drawing ##

  * [PGF/TikZ](http://sourceforge.net/projects/pgf/) for general drawing.
    * [Examples with pretty screenshots. Start here!](http://www.texample.net/tikz/examples/). One example:
> > [![](http://media.texample.net/tikz/examples/PNG/kalman-filter.png)](http://www.texample.net/tikz/examples/kalman-filter/).
    * [Manual](http://www.ctan.org/tex-archive/graphics/pgf/base/doc/generic/pgf/pgfmanual.pdf).
  * [Dot2tex](http://www.fauskes.net/code/dot2tex/) for drawing graphs using [GraphViz](http://www.graphviz.org/) and [TikZ](http://sourceforge.net/projects/pgf/). Example:
> > [![](http://www.fauskes.net/media/code/dot2tex/img/fsm1.png)](http://www.fauskes.net/code/dot2tex/)
  * [Gnuplot TikZ](http://peter.affenbande.org/gnuplot/) for generating TikZ plots with [Gnuplot](http://gnuplot.sourceforge.net/). Example:
> > [![](http://media.texample.net/tikz/examples/PNG/gnuplot-tikz-terminal.png)](http://www.texample.net/tikz/examples/gnuplot-tikz-terminal/)

## Pseudocode ##

  * [algorithm2e](https://docs.google.com/viewer?url=http://www.ctan.org/tex-archive/macros/latex/contrib/algorithm2e/algorithm2e.pdf) seems like the best option. Example:
[![](http://www.lirmm.fr/~fiorio/AlgorithmSty/ex_algo_33.jpg)](http://www.lirmm.fr/~fiorio/miscellaneous/algorithm2e.html)
  * See [Wikibooks](http://en.wikibooks.org/wiki/LaTeX/Algorithms_and_Pseudocode) for other (lesser IMO) options.

## Real code ##

  * [Pygments](http://pygments.org/). Python syntax highlighter.
  * [minted latex package](http://tug.ctan.org/tex-archive/macros/latex/contrib/minted/). Uses Pygments. [Manual](https://docs.google.com/viewer?url=http://mirror.ctan.org/macros/latex/contrib/minted/minted.pdf). Example:
> > ![http://page.mi.fu-berlin.de/krudolph/stuff/pi-in-the-sky?example.png](http://page.mi.fu-berlin.de/krudolph/stuff/pi-in-the-sky?example.png)
  * [listings latex package](https://docs.google.com/viewer?url=http://mirror.ctan.org/macros/latex/contrib/listings/listings.pdf) Examples:
> > [![](http://www.schneeflocke.net/latex/listings.png)](http://www.schneeflocke.net/latex/listings.tex)
> > [![](http://img9.imageshack.us/img9/1341/listing.png)](http://stackoverflow.com/questions/741985/latex-source-code-listing-like-in-professional-books)

  * On stackoverflow:
    * http://stackoverflow.com/questions/741985/latex-source-code-listing-like-in-professional-books
    * http://stackoverflow.com/questions/1966425/source-code-highlighting-in-latex
    * http://stackoverflow.com/questions/300521/latex-package-to-do-syntax-highlighting-of-code-in-various-languages