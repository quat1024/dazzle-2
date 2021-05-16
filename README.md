Dazzle 2
========

welcome to dazzle 2, ~~the "i don't want to ship hundreds of json files this time" edition~~ i am now shipping hundreds of json files

fabric 1.16.5 

## building

You will find some gradle tasks under the `d2` category.

`runGenerator`: launch the game, spew a bunch of generated assets and data into `src/gen_out/resources`, and exit.
`cleanGeneratedResources`: delete `src/gen_out/resources`.

These run real honest-to-goodness vanilla datagens (no more string replacement hackery, yahoo). due to source-set magic i cribbed from botania the generated resources can stay in that directory too.

things that are generated:

* blockstates for lamps and flare lights
* item models for lamps and flare lights (parents to the block model)
* language entries for lamps and flare lights
  * most are placed outside of the generated resources folder, in the main mod's `en_us.json` file (see note below)
  * `en_au`, `en_ca`, `en_gb`, and `en_nz` jsons are also generated, containing the entries that differ from en_us (gray/grey)
* recipes and recipe advancements for lamps and flare lights

## Quick note on `en_us.json`

You can't ship two language `.json`s for the same language in the mod, but i only want to generate some lang entries, so i made a compromise:

running the datagens has a side-effect of overwriting `en_us.json` to include generated data at the bottom. This avoids a manual copy-and-paste step where you have to paste in generated item names.

I make an effort to preserve the JSON formatting through the round-trip, including blank-lines. However it's kind of buggy so the json object inside `en_us.json` cannot end with a blank line, or it will fail to parse. The file has to end with "closing quote", "*exactly one* newline", then `}`.

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