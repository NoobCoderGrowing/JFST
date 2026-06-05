import com.google.common.collect.HashMultimap;

import java.util.*;


public class FST {

    private fstNode root;
    private fstNode endNode;


    private HashMultimap<String, fstNode> nodeMap;

    public FST(){
        root = new fstNode("\"");
        endNode = new fstNode("\"");
        root.setRoot(true);
        endNode.setEndNode(true);
        nodeMap = HashMultimap.create();
    }


    public Triple<Integer, fstNode, Integer> findPrefix(String[] phrase){
        fstNode currentNode = root;
        // find common prefix
        int i = 0;
        int currentValue = 0;// cumulated prefix old value
        for (;i < phrase.length; i++) {
            if(currentNode.contains(phrase[i])){
                fstEdge edge = currentNode.getChildren().get(phrase[i]);
                currentValue += edge.getOutput();
                currentNode = edge.getChild();
                continue;
            }
            break;
        }
        Triple result = new Triple<>(i, currentNode, currentValue);
        return result;
    }

    public void freezeNode(fstNode node){
        if(node.isEndNode()){
            return;
        }
        if(node.isFinal()){
            return;
        }
        Collection<fstEdge> edges = node.getChildren().values();
        for (fstEdge edge: edges){
            fstNode curNode = edge.getChild();
            curNode.setFinal(true);
            nodeMap.put(curNode.getWord(),curNode);
            freezeNode(curNode);
        }
    }


    public void link2Suffix(fstNode suffixStart, long[] restValue, fstNode currentNode ){
        fstEdge newEdge = new fstEdge();
        newEdge.setOutput(restValue[0]);
        newEdge.setChildAncestor(suffixStart, currentNode);
        currentNode.bindChildAncestor(suffixStart, newEdge);
        restValue[0] = 0;
    }


    public fstNode createNewNode(String[] phrase, int[] currentIndex, fstNode currentNode, int prefixIndex, long[] restValue){
        fstNode newNode = new fstNode(phrase[currentIndex[0]]);
        fstEdge newEdge = new fstEdge();
        newEdge.setChildAncestor(newNode, currentNode);
        if(currentIndex[0]==prefixIndex) {
            newEdge.setOutput(restValue[0]);
            restValue[0] = 0;
        }
        // double-way bind
        currentNode.bindChildAncestor(newNode, newEdge);
        currentIndex[0] += 1;
        return newNode;
    }

    // phrases should be sorted alphabetically ascending
    // 1. Put output at first edge
    // 2. If no suffix found or there is new node in between, put additional output at the edge to the first new node
    // 2. if suffix found and there is no new node, put additional output at the edge to the suffix start
    public void addPhrase(long newValue, String[] phrase){
        Triple triple = findPrefix(phrase);
        int prefixIndex = (Integer) triple.getFirst();
        fstNode currentNode = (fstNode) triple.getSecond();
        int currentValue = (Integer) triple.getThird();

        // freeze all child of the last node of prefix
        freezeNode(currentNode);


        int[] currentIndex = new int[]{prefixIndex};
        long[] restValue = new long[]{newValue - currentValue};
        // create new node until suffix found
        fstNode suffixStartNode = null;
        while (currentIndex[0] < phrase.length){
            suffixStartNode = findSuffix(currentIndex, phrase);
            if(suffixStartNode!=null){
                break;
            }else{
                currentNode = createNewNode(phrase, currentIndex, currentNode, prefixIndex, restValue);
            }
        }
        if(suffixStartNode!=null){ //if suffix found
            link2Suffix(suffixStartNode, restValue, currentNode);
        }else{ //  if no suffix found, link to end node
            fstEdge lastEdge = new fstEdge();
            lastEdge.setChildAncestor(endNode, currentNode);
            currentNode.bindChildAncestor(endNode, lastEdge);
        }
    }
    public boolean isEqual(String[] phrase, fstNode curNode, int phraseIndex){
        if(curNode.isEndNode()){
            return true;
        }
        if(curNode.getChildren().size()>1){
            return false;
        }

        if(phraseIndex == phrase.length){
            return false;
        }

        if(phrase[phraseIndex].equals(curNode.getWord())==false){
            return false;
        }
        for (fstEdge edge:curNode.getChildren().values()) {
            curNode = edge.getChild();
        }

        return isEqual(phrase, curNode, ++phraseIndex);
    }


    public fstNode findSuffix(int[] phraseIndex, String[] phrase){
        String key = phrase[phraseIndex[0]];
        if(nodeMap.containsKey(key)==false){
            return null;
        }
        Set<fstNode> nodes = nodeMap.get(key);

        for (fstNode suffixStartNode: nodes) {
            if(isEqual(phrase,suffixStartNode,phraseIndex[0])){
                return suffixStartNode;
            }

        }
        return null;
    }



    public fstPair<ArrayList<fstNode>, Long> searchPrefix(String[] phrase){
        ArrayList<fstNode> path = new ArrayList<>();
        long pathOutput = 0;
        fstNode currentNode = root;
        for(String token: phrase){
            if(!currentNode.getChildren().containsKey(token)){
                // -1 means no path found
                return null;
            }
            fstEdge currentEdge = currentNode.getChildren().get(token);
            pathOutput += currentEdge.getOutput();
            currentNode = currentNode.getChildren().get(token).getChild();
            path.add(currentNode);
        }
        return new fstPair<>(path, pathOutput);
    }

    public fstPair<ArrayList<fstNode>, Long> search(String[] phrase){
        if(phrase.length==0){
            return null;
        }
        ArrayList<fstNode> path = new ArrayList<>();
        long pathOutput = 0;
        fstNode currentNode = root;
        for(String token: phrase){
            if(!currentNode.getChildren().containsKey(token)){
                // -1 means no path found
                return null;
            }
            fstEdge currentEdge = currentNode.getChildren().get(token);
            pathOutput += currentEdge.getOutput();
            currentNode = currentNode.getChildren().get(token).getChild();
            path.add(currentNode);
        }
        if(currentNode.getChildren().values().iterator().next().getChild()!=endNode){
            return null;
        }
        return new fstPair<>(path, pathOutput);
    }



    public List<fstPair<List<fstNode>, Long>> fuzzySearchPrefix(String[] prefix){
        List<fstPair<List<fstNode>, Long>>  results = new ArrayList<>();


        fstPair<ArrayList<fstNode>, Long> searchRet = searchPrefix(prefix);
        if(searchRet == null){// if no prefix found, return empty list
            return  new ArrayList<>();
        }
        ArrayList<fstNode> path = searchRet.getKey();
        long pathValue = searchRet.getValue();
        collectPrefixPaths(path.get(path.size()-1), results, pathValue, path);
        return results;
    }

    public void searchSuffix(String[] suffix, List<fstPair<List<fstNode>, Long>> results,
                                                       List<fstNode> path, fstNode node, long output, int index){

        if(index==0){ // if suffix path found
            if(node.ancestors.containsKey(suffix[0])){
                Set<fstEdge> edges = node.ancestors.get(suffix[0]);
                for (fstEdge edge: edges) {
                    fstNode currentNode = edge.getAncestor();
                    ArrayList<fstNode> newPath = new ArrayList<>(path);
                    newPath.add(currentNode);
                    results.add(new fstPair<>(newPath, output + edge.getOutput()));
                }
            }
            return;
        }
        Set<fstEdge> edges = node.ancestors.get(suffix[index]);
        for (fstEdge edge: edges) {
            long currentOutput = output + edge.getOutput();
            fstNode currentNode = edge.getAncestor();
            List<fstNode> newPath = new ArrayList<>(path);
            newPath.add(currentNode);
            searchSuffix(suffix, results, newPath, currentNode, currentOutput+output, index-1);
        }
    }

    public void backSearchRecursive(String[] suffix, fstPair<List<fstNode>, Long> ret, List<fstNode> path, fstNode node,
                                                         long output, int index){
        if(index==0){ // if suffix path found
            if(node.ancestors.containsKey(suffix[0])){
                Set<fstEdge> edges = node.ancestors.get(suffix[0]);
                for (fstEdge edge: edges) {
                    output+=edge.getOutput();
                    fstNode currentNode = edge.getAncestor();
                    fstEdge prevEdge = currentNode.getAncestors().values().iterator().next();
                    if(prevEdge.getAncestor()!=root){
                        return;
                    }

                    ArrayList<fstNode> newPath = new ArrayList<>(path);
                    newPath.add(currentNode);
                    Collections.reverse(newPath);
                    // adding last output of the edge to the root
                    output+=prevEdge.getOutput();
                    ret.setKey(newPath);
                    ret.setValue(output);
                }
            }
            return;
        }
        Set<fstEdge> edges = node.ancestors.get(suffix[index]);
        for (fstEdge edge: edges) {
            long currentOutput = output + edge.getOutput();
            fstNode currentNode = edge.getAncestor();
            List<fstNode> newPath = new ArrayList<>(path);
            newPath.add(currentNode);
            backSearchRecursive(suffix, ret, newPath, currentNode, currentOutput+output, index-1);
        }

    }

    public fstPair<List<fstNode>, Long>  backSearch(String[] phrase){
        List<fstNode> path = new ArrayList<>();
        fstPair<List<fstNode>, Long> ret = new fstPair<List<fstNode>, Long>(new ArrayList<>(), 0l);
        backSearchRecursive(phrase, ret, path, endNode, 0, phrase.length-1);
        return ret;
    }



    public List<fstPair<List<fstNode>, Long>> fuzzySearchSuffix(String[] suffix){
        List<fstPair<List<fstNode>, Long>>  results = new ArrayList<>();
        List<fstPair<List<fstNode>, Long>>  suffixPaths = new ArrayList<>();
        List<fstNode> path = new ArrayList<>();
        int index = suffix.length - 1;
        searchSuffix(suffix, suffixPaths , path, endNode,0, index);

        for (fstPair<List<fstNode>, Long> pathPair: suffixPaths){
            List<fstNode> suffixPath = pathPair.getKey();
            fstNode suffixStart = suffixPath.get(suffixPath.size()-1);
            collectSuffixPaths(results, pathPair, suffixStart);
        }

        return results;
    }

    public void collectSuffixPaths(List<fstPair<List<fstNode>, Long>> results,
                                   fstPair<List<fstNode>, Long> pathPair, fstNode node){
        if(node.isRoot()){
            pathPair.getKey().remove(pathPair.getKey().size()-1);// remove start node
            Collections.reverse(pathPair.getKey());
            results.add(pathPair);
            return;
        }
        HashMultimap<String, fstEdge> ancestors = node.getAncestors();
        for (fstEdge edge: ancestors.values()) {
            fstNode ancestor = edge.getAncestor();
            long output = edge.getOutput();
            List<fstNode> newPath  = new ArrayList<>(pathPair.getKey());
            newPath.add(ancestor);
            long newOutput = pathPair.getValue() + output;
            fstPair<List<fstNode>, Long> newPathPair = new fstPair<>(newPath, newOutput);
            collectSuffixPaths(results, newPathPair, ancestor);
        }
    }



    public void collectPrefixPaths(fstNode startNode, List<fstPair<List<fstNode>, Long>> results, long pathValue, List<fstNode> path){
        if(startNode.isEndNode()){
            path.remove(path.size()-1);
            results.add(new fstPair<>(path, pathValue));
            return;
        }
        for(fstEdge currentEdge: startNode.getChildren().values()){
            List<fstNode> currentPath = new ArrayList<>(path);
            long currentPathValue = pathValue + currentEdge.getOutput();
            fstNode currentNode = currentEdge.getChild();
            currentPath.add(currentNode);
            collectPrefixPaths(currentNode, results, currentPathValue, currentPath);
        }

    }

    public void build(ArrayList<fstPair<String[], Long>> input){
        for (fstPair<String[], Long> entry: input) {
            addPhrase(entry.getValue(), entry.getKey());
        }
        nodeMap.clear();
    }

}
