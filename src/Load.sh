#!/bin/bash

sort -fu ads.txt -o ads.txt
sort -fu terms.txt -o terms.txt
sort -fu prices.txt -o prices.txt
sort -fu pdates.txt -o pdates.txt

cat ads.txt | ./break.pl | db_load -T -t hash ad.idx
cat terms.txt | ./break.pl | db_load -T -t btree te.idx
cat prices.txt | ./break.pl | db_load -T -t btree pr.idx
cat pdates.txt | ./break.pl | db_load -T -t btree da.idx
