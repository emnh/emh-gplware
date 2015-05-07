# Source for bash script that generates the graph #

http://emh-gplware.googlecode.com/hg/sc2-unit-graph/sc2-graph.sh

It works by downloading web pages and unit thumbnails from Battle.net unit guide, processing these to extract the data I'm interested in, write a dot file and render the graph using Graphviz.

# Starcraft 2 Unit Counter Graph #

Below is what the finished graph looks like. The layout is automatically generated and could be improved by manual tweaking. There's an arrow pointing from unit A to unit B if unit A is strong against unit B. This means that on the page for A, B was listed under "Strong Against", or that on the page for B, A was listed under "Weak Against".

This render is generated using Graphviz neato:

![http://alexis.lart.no/emh/sc2-neato-splines.png](http://alexis.lart.no/emh/sc2-neato-splines.png)

Here's another render using Graphviz fdp:

![http://alexis.lart.no/emh/sc2-fdp-splines.png](http://alexis.lart.no/emh/sc2-fdp-splines.png)

And using Graphviz circo:

![http://alexis.lart.no/emh/sc2-circo.png](http://alexis.lart.no/emh/sc2-circo.png)

# Starcraft 2 Unit Counter Pretty Wallpapers #

Here is another way of doing it, by Saucy. I found about these after making mine.
They were posted at http://eu.battle.net/sc2/en/forum/topic/441825881?page=1 . They're pretty sweet:

![http://saucy.se/image/misc/Terran.jpg](http://saucy.se/image/misc/Terran.jpg)
![http://saucy.se/image/misc/Protoss.jpg](http://saucy.se/image/misc/Protoss.jpg)
![http://saucy.se/image/misc/Zerg.jpg](http://saucy.se/image/misc/Zerg.jpg)

# Timed tech trees #

This is also cool, by Econael, from http://eu.battle.net/sc2/en/forum/topic/283411881#1:

![http://j.imagehost.org/0522/timed_tech_trees.gif](http://j.imagehost.org/0522/timed_tech_trees.gif)