package merger;

import java.util.LinkedList;
import java.util.Observable;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

public class MergeVisitor 
//#conflictsAnalyzer
extends Observable
//end of #conflictsAnalyzer
{

	private LinkedList<MergerInterface> mergerList = new LinkedList<MergerInterface>();

	public void registerMerger(MergerInterface merger) {
		mergerList.add(merger);
	}

	private LinkedList<MergerInterface> getMergerList() {
		return mergerList;
	}

	public void visit(FSTNode current) {
		if(current instanceof FSTNonTerminal) {
			for(FSTNode child : ((FSTNonTerminal)current).getChildren())
				visit(child);
		} else if(current instanceof FSTTerminal) {
			for(MergerInterface merger : getMergerList()) {
				try {

					if(((FSTTerminal)current).getBody().contains(FSTGenMerger.MERGE_SEPARATOR)) {
						merger.merge((FSTTerminal)current);

						//#conflictsAnalyzer
						//perform conflict pattern analysis and removal of spacing conflicts
						String nodeBody = ((FSTTerminal)current).getBody();
						if(nodeBody.contains(FSTGenMerger.MERGE_SEPARATOR) || nodeBody.contains(FSTGenMerger.DIFF3MERGE_SEPARATOR)) {
							setChanged();
							notifyObservers(current);
						}
						
						//mark merges without conflicts with contributions from each revision
						if(!(nodeBody.contains(FSTGenMerger.MERGE_SEPARATOR) || nodeBody.contains(FSTGenMerger.DIFF3MERGE_SEPARATOR))){
							
						}else{
							
						}
						//end of #conflictsAnalyzer
					}

				} catch (ContentMergeException e) {
					System.err.println(e.toString());
				} 
			}

		} else {
			System.err.println("MergerVisitor: node is neither non-terminal nor terminal!");			
		}
	}
}
