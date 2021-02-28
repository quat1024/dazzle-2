i am so sorry for this package lmao

It's really not good, was a first crack at this "automatically generating resources on-demand" thing and I'm not sure it worked very well

Some things I'd like to change:

* It's persnickety about parsing things out of a regex every time. a more purposebuilt abstraction for lamp styles would be groovy
* Also having to handle analog and digital lamps together isn't great
* I'd like to be able to "mount" a provider at a particular point in the virtual filesystem so I don't have to deal with managing fragile string prefixes and suffixes all the time.

It does achieve one goal, though, of not using a ton of memory when it's not in use. Theoretically I could include a "cache clear" system too, to drop all of this stuff when the game is all done loading. Will have to check the viability of that.

I'm writing out this list instead of just going out and fixing it, because my brain's a bit fizzy right now and I don't think i'd do it correctly.