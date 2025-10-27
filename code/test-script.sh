#!/bin/bash

case $1 in
	"1" )	echo CoarseGrainedListBasedSet
			OUTPUT="CoarseGrainedListBasedSet"
	;;
	"2" )	echo HandOverHandListIntSet
			OUTPUT="HandOverHandListIntSet"
	;;
	"3" )	echo LazyLinkedListSortedSet
			OUTPUT="LazyLinkedListSortedSet"
	;;
    *)		echo "Specify algorithm e.g. 'test-script.sh 1' from 1,2,3" >&2
            # If the script is being sourced, `BASH_SOURCE[0] != $0`; use return to avoid closing the SSH session.
            if [ "${BASH_SOURCE[0]}" != "$0" ]; then
              return 1
            else
              exit 1
            fi
esac

echo "Who I am: $OUTPUT on `uname -n`"
echo "started on" `date`

# num of threads loop
for i in 1 4 6 8 10 12
do
	# update ratio loop
    for j in 0 10 100
	do
		# list size loop
        for k in 100 1000 10000
        do
            r=$((2 * k))
            echo "→ $OUTPUT	threads: $i	update-ratio: $j list-size: $k"
		    java -cp bin contention.benchmark.Test -b linkedlists.lockbased.$OUTPUT -d 2000 -t $i -u $j -i $k -r $r -W 0 | grep Throughput
        done
    done
	# wait
done
echo "finished on" `date`
echo "DONE \o/"

