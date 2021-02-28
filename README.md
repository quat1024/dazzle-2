Dazzle 2
========

welcome to dazzle 2, the "i don't want to ship hundreds of json files this time" edition

fabric 1.16.4, yoinks a mixin from ARRP btw

## current featureset

* an *all-new type of light!!!!*, we're up to 5 types of lights now!
* as usual they come in 16 colors, digital/analog (craft with a comparator), regular/inverted (click with a redstone torch) variations
* the Light Sensor. conceptually similar to a daylight sensor, but places like an observer, and detects blocklight instead of sky light

## todo

should really clean up the resource-gen code before i go adding more repetitive 16-colors content lmao. anyway

* more "natural" light sources, e.g. more types of lanterns and torches
  * something animated could be fun, like a lantern with a fire in it
  * Spend MORE on candles
  * modern-style light bulbs and lampshades, maybe
  * art is hard ;)
* invisible lights
  * but this time, with a craftable item to place and break them manually
  * also i need better UX around em, it should be possible to make them visible under some condition
  * was thinking about using sone Canvas features to give them a blockmodel that is visible under a "holding an invisible light item" condition, idk how well that will work. i can always fall back to using something similar to barrier particles. don't wanna use a block entity renderer.
* port the dim redstone torch
* port the particle light flashes (maybe give em a block loot table this time lmao) 
* port the panel lights, that project a bunch of invisible lights in a line when powered
