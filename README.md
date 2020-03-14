# gb-CC-bruteforce
==========

Most of the code is from stringflow and entrpntr, it has been adapted to work for Crystal Clear.

Original README (with minor relevant fixes) below

#### Overview

This is base code mimicking [entrpntr's codebase](http://github.com/entrpntr/gb-rta-bruteforce) using JNA rather than JNI. 

#### Installation for Windows

Build `libgambatte.dll` (Instructions can be found [here](https://github.com/pokemon-speedrunning/gambatte-speedrun))  
Clone the repository, create a new folder called `libgambatte` and put the previously built dll in there.  
Create another folder called `roms` and put `gbc_bios.bin` as well as `pokecrystal.gbc` in there.  
After that fire up your favorite Java IDE, add the JNA library to the classpath and start using it.
