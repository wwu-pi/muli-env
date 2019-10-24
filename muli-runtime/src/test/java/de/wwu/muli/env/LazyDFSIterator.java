package de.wwu.muli.env;

import de.wwu.muli.searchtree.*;

import java.util.List;
import java.util.ListIterator;
import java.util.Spliterator;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class LazyDFSIterator implements Spliterator<Object> {
    private final Stack<ST> nodes;

    public LazyDFSIterator(ST st) {
        nodes = new Stack<>();
        nodes.push(st);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Object> action) {
        if (nodes.isEmpty()) {
            return false;
        }
        ST tree = nodes.pop();
        if (tree instanceof Fail) {
            return tryAdvance(action);
        } else if (tree instanceof de.wwu.muli.searchtree.Exception) {
            action.accept(null);
            return true;
        } else if (tree instanceof Value) {
            action.accept(((Value)tree).value);
            return true;
        } else if (tree instanceof Choice) {
            // Push search trees in reverse order so they will be popped from left-to-right.
            List<UnevaluatedST> subtrees = ((Choice) tree).getSts();
            ListIterator<UnevaluatedST> stIt = subtrees.listIterator(subtrees.size());

            while (stIt.hasPrevious()) {
                nodes.push(stIt.previous());
            }
            return tryAdvance(action);
        } else if (tree instanceof UnevaluatedST) {
            UnevaluatedST uneval = (UnevaluatedST) tree;
            ST result;
            if (uneval.isEvaluated()) {
                result = uneval.getEvaluationResult();
                nodes.push(result);
            }
            return tryAdvance(action);
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public Spliterator<Object> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return 0;
    }

    public static Stream<Object> stream(ST tree) {
        return StreamSupport.stream(new LazyDFSIterator(tree), false);
    }
}
