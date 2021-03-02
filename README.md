Dazzle 2
========

welcome to dazzle 2, the "i don't want to ship hundreds of json files this time" edition

fabric 1.16.4, yoinks a mixin from ARRP btw

## current featureset

* an *all-new type of light!!!!*, we're up to 5 types of lights now!
* as usual they come in 16 colors, digital/analog (craft with a comparator), regular/inverted (click with a redstone torch) variations
* the Light Sensor. conceptually similar to a daylight sensor, but places like an observer, and detects blocklight instead of sky light
* invisible lights
* light panels, which project light up to 45 blocks away depending on signal strength
  * should be a lot less buggy than the ones in dazzle 1, give em a look!
* dim redstone torch - emits light level 3 and redstone signal 1
* colored flare lights, emit happy little particles 

## todo

should really clean up the resource-gen code before i go adding more repetitive 16-colors content lmao
* tbh i basically wanna make arrp 2 "generates stuff on-demand" edition
* the code is patently terrible, i left some notes in a readme in that package of the source tree

anyway

* more "natural" light sources, e.g. more types of lanterns and torches
  * something animated could be fun, like a lantern with a fire in it
  * ~~Spend MORE on candles~~i forgot they're in 1.17 lol
  * modern-style light bulbs and lampshades, maybe
  * art is hard ;)
* some way to see the invisible lights (consider: like barrier blocks)