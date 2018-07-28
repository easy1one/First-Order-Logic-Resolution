# First-Order-Logic-Resolution


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
6
F(Joe)
H(John)
~H(Alice)
~H(John)
G(Joe)
G(Tom)
14
~F(x) | G(x)
~G(x) | H(x)
~H(x) | F(x)
~R(x) | H(x)
~A(x) | H(x)
~D(x,y) | ~H(y)
~B(x,y) | ~C(x,y) | A(x)
B(John,Alice)
B(John,Joe)
~D(x,y) | ~Q(y) | C(x,y)
D(John,Alice)
Q(Joe)
D(John,Joe)
R(Tom)
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
FALSE
TRUE
TRUE
FALSE
FALSE
TRUE
```
