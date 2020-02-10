package statistics;

public class StatisticsEntry {

	public static final String[] HEADERS = new String[] { "#Buildings", "#Blocks", "#RoadSegments", "#Crossroads",
			"#Nodes", "#links" };

	public int buildings = 0;
	public int blocks = 0;
	public int roadSegments = 0;
	public int crossroads = 0;

	public int nodes = 0;
	public int links = 0;

	public StatisticsEntry() {

	}

	public StatisticsEntry(String[] data) {
		buildings = Integer.parseInt(data[0]);
		blocks = Integer.parseInt(data[1]);
		roadSegments = Integer.parseInt(data[2]);
		crossroads = Integer.parseInt(data[3]);

		nodes = Integer.parseInt(data[4]);
		links = Integer.parseInt(data[5]);
	}

	public String[] toStringArray() {
		String[] res = new String[6];

		res[0] = "" + buildings;
		res[1] = "" + blocks;
		res[2] = "" + roadSegments;
		res[3] = "" + crossroads;

		res[4] = "" + nodes;
		res[5] = "" + links;

		return res;
	}

}
