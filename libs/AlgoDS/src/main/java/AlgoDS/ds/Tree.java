package AlgoDS.ds;


public interface Tree<K> {
    void insert(K k);
    boolean search(K k);
    @SuppressWarnings("unused")
    void delete(K k);
}
