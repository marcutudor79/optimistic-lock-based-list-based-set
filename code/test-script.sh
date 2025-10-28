#!/bin/bash

# array of algorithm names
OUTPUT=(CoarseGrainedListBasedSet HandOverHandListIntSet LazyLinkedListSortedSet)
echo "started on" `date`

echo "Starting with fixed update ratio 10% and varying list size"
echo "--------------------------------------------------------------------"
# algorithm loop - each graph
for algo in 0 1 2
do
    echo "→ Running: ${OUTPUT[$algo]} with update ratio 10"
    # each line in graph
    for k in 100 1000 10000
    do
        echo "list-size: $k"
        # each point in line
        for i in 1 4 6 8 10 12
        do
            r=$((2 * k))
            echo "threads: $i "
            java -cp bin contention.benchmark.Test -b linkedlists.lockbased.${OUTPUT[$algo]} -d 2000 -t $i -u 10 -i $k -r $r -W 0 | grep Throughput
        done
    done
done
echo "--------------------------------------------------------------------"
echo " "
echo "Starting with fixed list size 100 and varying update ratios"
echo "--------------------------------------------------------------------"
# algorithm loop - each graph
for algo in 0 1 2
do
    echo "→ Running: ${OUTPUT[$algo]} with list size 100"
    # each line in graph
    for j in 0 10 100
    do
        echo "update ratio: $j"
        for i in 1 4 6 8 10 12
        do
            echo "threads: $i "
            java -cp bin contention.benchmark.Test -b linkedlists.lockbased.${OUTPUT[$algo]} -d 2000 -t $i -u $j -i 100 -r 200 -W 0 | grep Throughput
        done
    done
done
echo "--------------------------------------------------------------------"
echo " "
echo "Starting with fixed update ratio of 10% and list size 1000"
echo "--------------------------------------------------------------------"
# algorithm loop - each graph
for algo in 0 1 2
do
    echo "→ Running: ${OUTPUT[$algo]} with update ratio 10% and lise size 1000"
    # each point in line
    for i in 1 4 6 8 10 12
    do
        echo "threads: $i "
        java -cp bin contention.benchmark.Test -b linkedlists.lockbased.${OUTPUT[$algo]} -d 2000 -t $i -u 10 -i 1000 -r 2000 -W 0 | grep Throughput
    done
done
echo "--------------------------------------------------------------------"
echo " "
echo "finished on" `date`
echo "DONE \o/"

