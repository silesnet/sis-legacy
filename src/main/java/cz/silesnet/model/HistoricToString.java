package cz.silesnet.model;

/**
 * An interface to mark classes that provide getHistoricToString() which is used
 * when computing diffs.
 * 
 * @author Richard Sikora
 * 
 */
public interface HistoricToString {

	// ~ Methods
	// ----------------------------------------------------------------

	public String getHistoricToString();
}