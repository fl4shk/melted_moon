#!/bin/bash
for file in ../../hw/gen/meltedMoonDebug/*.bin; do
	rm $(basename $file)
	ln -s $file
done
