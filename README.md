# First-Order-Logic-Resolution
Find out the True/False of a given statement based on the knowledge sets using first-order logic resolution
[first order logic](https://en.wikipedia.org/wiki/First-order_logic)

## Highlevel description
...

# Code
## input.txt in inputs folder
> <NQ = NUMBER OF QUERIES>
> <QUERY 1>
> ...
>  <QUERY NQ>
> <NS = NUMBER OF GIVEN SENTENCES IN THE KNOWLEDGE BASE>
> <SENTENCE 1>
> ...
> <SENTENCE NS>

- __Example 1,__
```
2
Ancestor(Liz,Billy)
Ancestor(Liz,Joe)
6
Mother(Liz,Charley)
Father(Charley,Billy)
~Mother(x,y) | Parent(x,y)
~Father(x,y) | Parent(x,y)
~Parent(x,y) | Ancestor(x,y)
~Parent(x,y) | ~Ancestor(y,z) | Ancestor(x,z)
```

## firstLogicOrder.java
- ...
```...
```

## output.txt in outputs folder
> <ANSWER 1>
> ...
> <ANSWER NQ>

- __Example 1__ <br />
```
TRUE
FALSE
```
