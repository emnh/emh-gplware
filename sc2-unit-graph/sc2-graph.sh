#!/bin/bash

wget -c http://eu.battle.net/sc2/en/game/unit/$unit

# get units
sed -n "/Core.goTo/s/.*'\([^']*\)'.*/\1/p" index.html > units.txt

# download unit pages
for unit in $(<units.txt); do
    wget -c http://eu.battle.net/sc2/en/game/unit/$unit
done

# download css and thumbs
wget -c 'http://eu.battle.net/sc2/static/css/game/game-common.css?v2'
wget -c 'http://eu.battle.net/sc2/static/images/game/unit-thumbs-table.gif'

# extract unit images from thumbs sprite
thumbs=unit-thumbs-table.gif
css='game-common.css?v2'
mkdir -p img
for unit in $(<units.txt); do
    w=80
    h=72 #~90 with title
    xy=$(sed -n "/.unit-thumb .$unit/s/.*position:\([^;]*\);.*/\1/p" $css | grep -o '[0-9]*')
    x=$(echo $xy | cut -f1 -d" ")
    y=$(echo $xy | cut -f2 -d" ")
    geometry=${w}x${h}+$x+$y
    convert -crop $geometry $thumbs img/$unit.png
done

# extract strength and weaknesses
for unit in $(<units.txt); do
    sed -n '/Strong Against/,/<\/ul>/p' $unit |
    sed -n "/unit-thumb/s/.*href=\".\/\([^\"]*\)\".*/\1/p"  > $unit-strong.txt
    sed -n '/Weak Against/,/<\/ul>/p' $unit |
    sed -n "/unit-thumb/s/.*href=\".\/\([^\"]*\)\".*/\1/p"  > $unit-weak.txt
done

# generate graphviz dot file
(
cat << EOF
digraph {
  overlap = scale;
  splines = true;
  outputorder = edgesfirst;
  //bgcolor = "#02264D";
  //bgcolor = black;
  node [ color=none, shape = rect, label = "" ];
#  subgraph cluster_legend {
#    label = "Legend";
#    node [ shape = rect, label = "" ];
#    u1 -> u2 [ label = "Weak Against", color = red];
#    u3 -> u4 [ label = "Strong Against", color = green];
#  }
EOF
for unit in $(<units.txt); do
    echo "\"$unit\" [image=\"img/$unit.png\"];"
    for strong in $(<$unit-strong.txt); do
        #echo "  \"$unit\" -> \"$strong\" [color = green];"
        echo "  \"$unit\" -> \"$strong\";"
    done
    for weak in $(<$unit-weak.txt); do
        #echo "  \"$unit\" -> \"$weak\" [color = red];"
        echo "  \"$weak\" -> \"$unit\";"
    done
done
echo "}"
) > sc2.dot

# render dot file to images using different layouts
neato -Tpng sc2.dot -o sc2-neato-splines.png
fdp -Tpng sc2.dot -o sc2-fdp-splines.png
circo -Tpng sc2.dot -o sc2-circo.png
twopi -Tpng sc2.dot -o sc2-twopi.png
dot -Tpng sc2.dot -o sc2-dot.png
geeqie
