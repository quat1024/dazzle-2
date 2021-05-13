Dazzle 2
========

welcome to dazzle 2, ~~the "i don't want to ship hundreds of json files this time" edition~~ i am now shipping hundreds of json files

fabric 1.16.5 

## building

a lot of the assets/data for this mod are really really repetitive, so there's a gradle task `runGenerator` to generate them.

**it's NOT a "datagen"**, I'm sorry to get your hopes up if you're looking how to do datagens in fabric. it's just shitty handrolled string-replacement stuff.

process to update generated resources

* run that task
* run `clean` (for some reason the task compiles the mod lol)
* copy the contents of `src/gen/resources/en_us_include.json` into `src/main/resources/assets/dazzle/lang/en_us.json` (yeah yeah I could automate this maybe)

generated resources live in `src/gen/resources` and, due to source-set magic i cribbed from botania, can stay there.

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

anyway

* more "natural" light sources, e.g. more types of lanterns and torches
  * something animated could be fun, like a lantern with a fire in it
  * ~~Spend MORE on candles~~i forgot they're in 1.17 lol
  * modern-style light bulbs and lampshades, maybe
  * art is hard ;)
* some way to see the invisible lights (consider: like barrier blocks)