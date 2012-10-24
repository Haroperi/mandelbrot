
Mandelbrot.class: Mandelbrot.java Complex.java
	javac Mandelbrot.java Complex.java

.PHONY: clean run
	
run:
	appletviewer file:///Users/haro/work/report/complexfunc/mandelbrot/app.html

clean:
	rm *.class


