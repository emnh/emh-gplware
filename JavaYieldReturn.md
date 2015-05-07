# Edit: This is all obsolete #
Just use [Clojure](http://clojure.org). It's brilliant!

# Introduction #

I want this for Java: http://docs.python.org/tutorial/classes.html#generators.

Similar projects:
  * [Thread-based Java yield return](http://jimblackler.net/blog/?p=61)
  * [Byte-code engineering based Java yield return](http://chaoticjava.com/posts/implementation-details-for-java-yielder/)
  * [Relevant question #1 on Stackoverflow](http://stackoverflow.com/questions/1980953/is-there-a-java-equivalent-to-cs-yield-keyword)
  * [Relevant question #2 on Stackoverflow](http://stackoverflow.com/questions/2352399/yield-return-in-java)


# Simple sample implementation #

Known issues:
  * Not guaranteed to be bug free, but it has been tested.
  * If the iterator is not exhausted, such as an enhanced for loop exiting with break statement, the generator thread will be sleeping waiting for it's lock, preventing application termination because it waits for the thread. As a work-around use System.exit at your normal exit point. A better fix is to add a "break" method to Generator that shuts down the thread and require you to call it when you use break, and use Runtime.getRuntime().addShutdownHook() to emit a warning and shut down the thread at exit if you forget it. I will do this later and post new code.

## Generator class ##
```
package util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.Semaphore;

public abstract class Generator<ReturnType> extends Thread implements Iterator<ReturnType> {
	private ReturnType next;

	Semaphore callerLock = new Semaphore(0);
	Semaphore generatorLock = new Semaphore(0);

	public Generator() {
		start();
	}

	/**
	 * Override this and call yield(return) for each value.
	 */
	protected abstract void apply();

	@Override
	public boolean hasNext() {
		if (this.next == null) {
			try {
				this.next = next();
			} catch (NoSuchElementException e) {
				return false;
			}
		}
		return this.next != null;
	}

	@Override
	public ReturnType next() {
		// return cached
		if (this.next != null) {
			ReturnType ret_ = this.next;
			this.next = null;
			return ret_;
		}

		if (!this.isAlive()) {
			throw new NoSuchElementException();
		}
		// wake up generator
		this.generatorLock.release();
		// wait for generator
		this.callerLock.acquireUninterruptibly();

		ReturnType ret_ = this.next;
		this.next = null;
		return ret_;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	@Override
	public final void run() {
		// wait for caller
		this.generatorLock.acquireUninterruptibly();
		apply();
		// return to caller
		this.callerLock.release();
	}

	final protected void yield(ReturnType ret) {
		//System.out.printf("yield: %s\n", ret);
		this.next = ret;
		// wake up caller
		this.callerLock.release();
		// wait for caller
		this.generatorLock.acquireUninterruptibly();
	}
}
```

## Usage example ##

```
        public static class FileIterator extends Generator<File> {

		String path;

		public FileIterator(String path) {
			this.path = path;
		}

		@Override
		public void apply() {
			Stack<File> dirs = new Stack<File>();
			File root = new File(this.path);
			dirs.push(root);

			while (!dirs.empty()) {
				File curdir = dirs.pop();
				if (curdir == null) {
					System.out.println("wtf");
				}
				for (File f : curdir.listFiles()) {
					if (f.isDirectory()) {
						dirs.push(f);
					} else {
						yield(f);
					}
				}
			}
		}
	}

```