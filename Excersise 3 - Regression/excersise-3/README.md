# Excersise 3 - Logistic Regression

This Java program visualises the SSE and ROC curve of a logistic regression applied to the 'Pregnant' dataset, which is included in the 'Data Smart' textbook. The ROC curve/SSE when using the provided beta coefficients within the textbook is included as well, for reference.

A custom-made genetic algorithm is used to find the best array of beta coefficients. The multiple linear regression was done using external libraries, namely Jama and Apache Commons.

You can change the settings for the Genetic Algorithm (Crossover rate, mutation chance etc.) in the App.java file, starting at line 45.

Run using:

`cd excersise-3/` then `mvn exec:java`

The genetic algorithm might take a little while to finish, depending on the population size and the amount of generations that you use. 

Since the genetic algorithm is only a heuristic, your mileage may vary.