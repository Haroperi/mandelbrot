
Mandelbrot.class: Mandelbrot.java
	javac Mandelbrot.java

.PHONY: clean run
	
run: Mandelbrot.class
	appletviewer file:///Users/haro/work/report/complexfunc/mandelbrot/app.html

clean:
	rm *.class


