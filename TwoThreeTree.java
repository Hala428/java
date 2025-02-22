public class TwoThreeTree <T,K extends Comparable<K>> {
    protected Node<T, K> root;
    protected int sizeOfTree;

    public TwoThreeTree() {
        this.sizeOfTree = 0;
    }

    public Node<T, K> getRoot() {
        return root;
    }

    public int getSize() {
        return sizeOfTree;
    }

    public Node<T, K> search(Node<T, K> x, K key) {
        if (x.isLeaf()) {
            if (key.equals(x.key)) {
                return x;
            } else {
                return null;
            }
        }
        // 2 או 3 ילדים
        K leftKey = x.getLeft().getKey();
        K midKey = x.getMiddle().getKey();

        if (key.compareTo(leftKey) <= 0) {
            return search(x.getLeft(), key);
        } else if (key.compareTo(midKey) <= 0) {
            return search(x.getMiddle(), key);
        } else {
            return search(x.getRight(), key);
        }
    }

    private void updateKey(Node<T, K> x) {
        if (x == null) return;
        x.key = x.left.key;
        x.size = x.left.size;
        if (x.middle != null) {
            x.key = x.middle.key;
            x.size += x.middle.size;
        }
        if (x.right != null) {
            x.size += x.right.size;
            x.key = x.right.key;
        }
    }
    private Node<T, K> findPreviousLeaf(Node<T, K> x, K key) {
        Node<T, K> prev = null;
        while (x != null) {
            if (x.isLeaf()) {
                if (key.compareTo(x.key) > 0) {
                    prev = x; // Update prev if key is greater
                } else {
                    break; // Stop when we pass the correct position
                }
            }
            x = x.nextLeaf; // Move to the next leaf
        }
        return prev;
    }
    private void setChildren(Node<T, K> x,
                             Node<T, K> l,
                             Node<T, K> m,
                             Node<T, K> r) {
        x.left = l;
        x.middle = m;
        x.right = r;

        if (l != null){
            l.p = x;
        }
        if (m != null) {
            m.p = x;
        }
        if (r != null){
            r.p = x;
        }
        updateKey(x);

    }

    public Node<T, K> InsertAndSplit(Node<T, K> x, Node<T, K> z) {
        Node<T, K> left = x.left;
        Node<T, K> middle = x.middle;
        if (x.right == null) {
            if (z.key.compareTo(left.key) < 0) {
                setChildren(x, z, left, middle);
            } else if (z.key.compareTo(middle.key) < 0) {
                setChildren(x, left, z, middle);
            } else {
                setChildren(x, left, middle, z);
            }
            return null;
        }
        Node<T, K> right = x.right;
        Node<T, K> y = new Node<>(null, null, null, null, null);
        if (z.key.compareTo(left.key) < 0) {
            setChildren(x, z, left, null);
            setChildren(y, middle, right, null);
        } else if (z.key.compareTo(middle.key) < 0) {
            setChildren(x, left, z, null);
            setChildren(y, middle, right, null);
        } else if (z.key.compareTo(right.key) < 0) {
            setChildren(x, left, middle, null);
            setChildren(y, z, right, null);
        } else {
            setChildren(x, left, middle, null);
            setChildren(y, right, z, null);
        }
        return y;
    }

    public void insert(TwoThreeTree<T, K> T, Node<T, K> z) {
        Node<T, K> y = T.root;
        while (!y.isLeaf()) {
            if (z.key.compareTo(y.left.key) < 0) {
                y = y.left;
            } else if (z.key.compareTo(y.middle.key) < 0) {
                y = y.middle;
            } else {
                y = y.right;
            }

        }
        Node<T, K> x = y.p;
        z = InsertAndSplit(x, z);
        while (x != T.root) {
            x = x.p;
            if (z != null) {
                z = InsertAndSplit(x, z);
            } else {
                updateKey(x);
            }
        }
        if (z != null) {
            Node<T, K> w = new Node<>(null, null, null, null, null);
            setChildren(w, x, z, null);
            T.root = w;
        }
        T.sizeOfTree++;
        T.root.size = sizeOfTree;
    }

    public void deleteNode(Node<T, K> node) {
        node.key = null;
        node.data = null;
        node.size = 0;

    }

    public Node<T, K> Borrow_Or_Merge(Node<T, K> y) {
        Node<T, K> z = y.p;
        Node<T, K> x;
        if (y.equals(z.left)) {
            x = z.middle;
            if (x.right != null) {
                setChildren(y, y.left, x.left, null);
                setChildren(x, x.middle, x.right, null);
            } else {
                setChildren(x, y.left, x.left, x.middle);
                deleteNode(y);
                setChildren(z, x, z.right, null);
            }
            return z;

        }
        if (y.equals(z.middle)) {
            x = z.left;
            if (x.right != null) {
                setChildren(y, x.right, y.left, null);
                setChildren(x, x.left, x.middle, null);
            } else {
                setChildren(x, x.left, x.middle, y.left);
                deleteNode(y);
                setChildren(z, x, z.right, null);
            }
            return z;
        }
        x = z.middle;
        if (x.right != null) {
            setChildren(y, x.right, y.left, null);
            setChildren(x, x.left, x.middle, null);
        } else {
            setChildren(x, x.left, x.middle, y.left);
            deleteNode(y);
            setChildren(z, z.left, x, null);
        }
        return z;
    }

    public void Delete(TwoThreeTree<T, K> tree, Node<T, K> x) {
        Node<T, K> y = x.p;
        if (x == y.left) {
            setChildren(y, y.middle, y.right, null);
        } else if (x == y.middle) {
            setChildren(y, y.left, y.right, null);
        } else {
            setChildren(y, y.left, y.middle, null);
        }
        deleteNode(y);
        while (y != null) {
            if (y.middle != null) {
                updateKey(y);
                y = y.p;
            } else {
                if (y != tree.root) {
                    y = Borrow_Or_Merge(y);
                } else {
                    tree.root = y.left;
                    y.left.p = null;
                    deleteNode(y);
                    sizeOfTree--;
                    return;
                }
            }
        }
        sizeOfTree--;
    }

    public int SumOfStocks(Node<Price, Float> x, Float k) {
        if (x == null) {
            return 0;
        }
        if (x.isLeaf()) {
            if (x.key.compareTo(k) <= 0) {
                return (x.size);
            } else {
                return 0;
            }
        }
        if (k.compareTo(x.left.key) <= 0) {
            return SumOfStocks(x.left, k);
        } else if (k.compareTo(x.middle.key) <= 0) {
            return SumOfStocks(x.middle, k) + x.left.size;
        } else {
            return x.middle.size +
                    x.left.size + SumOfStocks(x.right, k);
        }
    }

    public int Rank(Node<Price, Float> x) {
        int rank = 1;
        Node<Price, Float> y = x.p;
        while (y != null) {
            if (x == y.middle) {
                rank = rank + y.left.size;
            } else if (x == y.right) {
                rank = rank + y.left.size + y.middle.size;
            }
            x = y;
            y = y.p;
        }
        return rank;
    }
    public Node<Price, Float> Select_Rec(Node<Price, Float> x, int i) {
        if (x.size < i) {
            return null;
        }
        if (x.isLeaf()) {
            return x;
        }
        int s_left = (x.left.size);
        int s_left_middle = x.middle.size + x.left.size;
        if (i <= s_left) {
            return Select_Rec(x.left, i);
        } else if (i <= s_left_middle) {
            return Select_Rec(x.middle, i - s_left);
        } else {
            return Select_Rec(x.right, i - s_left_middle);
        }
    }
}
