#!/bin/bash

psql -h localhost -p 32768 -U postgres -d postgres -f $1
