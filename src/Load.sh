#!/bin/bash

sort -fu ads.txt -o ads.txt
sort -fu terms.txt -o terms.txt
sort -fu prices.txt -o prices.txt
sort -fu pdates.txt -o pdates.txt

db_load -T -f ads.txt -t hash ad.idx
db_load -T -f terms.txt -t btree te.idx
db_load -T -f prices.txt -t btree pr.idx
db_load -T -f pdates.txt -t btree da.idx
