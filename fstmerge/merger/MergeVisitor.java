package merger;

import java.util.LinkedList;

import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;
import de.ovgu.cide.fstgen.ast.FSTTerminal;

//#conflictsAnalyzer
import conflictsAnalyzer.Conflict;
//end of #conflictsAnalyzer

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
						String nodeBody = ((FSTTerminal)current).getBody();
						if(nodeBody.contains(FSTGenMerger.MERGE_SEPARATOR) || nodeBody.contains(Conflict.DIFF3MERGE_SEPARATOR)) {
							setChanged();
							notifyObservers(current);
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
