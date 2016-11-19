# Practical XML: Exercise 01


## Pre Work

java -cp ./saxon9.jar net.sf.saxon.Query 

java -cp ./saxon9.jar net.sf.saxon.Query ex01_08a.xq

[Mondial XML instance](http://www.dbis.informatik.uni-goettingen.de/Mondial/mondial.xml)

 
## Commands

saxonXQ ex01_06.xq

saxonXQ ex01_07_de.xq > de.xml

xmllint -loaddtd -valid -noout de.xml


saxonXQ ex01_07_fr.xq > fr.xml

xmllint -loaddtd -valid -noout fr.xml


saxonXQ ex01_08a.xq word="Mont Blanc"
saxonXQ ex01_08a.xq word="Mont Blanc1"
saxonXQ ex01_08a.xq word="Himalaya"

saxonXQ ex01_08b.xq

saxonXQ ex01_09a.xq x=5
saxonXQ ex01_09c.xq x=5

saxonXQ ex01_10.xq rname="Aller"

saxonXQ ex01_10.xq rname="Ischim"
saxonXQ ex01_10.xq rname="Ob"
saxonXQ ex01_10.xq rname="Vuoksi"


Ischim

Ob



