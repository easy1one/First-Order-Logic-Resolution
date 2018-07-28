# First-Order-Logic-Resolution
Find out the True/False of a given statement based on the knowledge sets using first-order logic resolution<br />
ref. [first order logic](https://en.wikipedia.org/wiki/First-order_logic)

## Defined operators
- Knowledge bases which I created to handle each case contians sentences with the following defined operators:
NOT X     =>     ~X
X OR Y    =>     X | Y

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
Based on the given senteces in the knowledge bases in input.txt, firstLogicOrder will determine if the query can be inferred from the knowledge base or not for each query.
- To create knowledge base with Map
```
public static HashMap<String, ArrayList<Integer>> createKBmap()
```
- To determine if the query could be inferred
```
public static boolean getAnswer(String query)
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
