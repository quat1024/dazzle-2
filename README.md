Dazzle 2
========

welcome to dazzle 2, ~~the "i don't want to ship hundreds of json files this time" edition~~ i am now shipping hundreds of json files

fabric 1.16.5 

## building

You will find some gradle tasks under the `d2` category.

**it's not ~~yet~~ a "datagen"**, I'm sorry to get your hopes up if you're looking how to do datagens in fabric. it's just shitty handrolled string-replacement stuff.

`runGenerator`: launch the game, spew a bunch of generated assets and data into `src/gen_out/resources`, and exit.
`cleanGeneratedResources`: delete `src/gen_out/resources`.

make sure to copy the contents of `src/gen_out/resources/en_us_include.json` into `src/main/resources/assets/dazzle/lang/en_us.json` after running the generator, (yeah yeah, I could automate this, maybe)

due to source-set magic i cribbed from botania the generated resources can stay in that directory

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