import java.util.List;


public class SumCluster implements Comparable<SumCluster>{
	
	List<TestCase> list;
	
	float distanceFromFailedTestCases;
	float suspiciousDistance;
	
	float variance;
	int size;
	int ccCount;
	
	SumCluster(int size,int ccCount,float variance,float distance,float sd,List<TestCase> list)
	{
		this.list = list;
		this.size = size;
		this.ccCount = ccCount;
		this.variance = variance;
		this.distanceFromFailedTestCases = distance;
		this.suspiciousDistance = sd;
	}
	
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getCcCount() {
		return ccCount;
	}
	public void setCcCount(int ccCount) {
		this.ccCount = ccCount;
	}

	@Override
	public int compareTo(SumCluster o) {
		
		if (suspiciousDistance - o.suspiciousDistance < 0)
			return 1;
		else if (suspiciousDistance - o.suspiciousDistance > 0)
			return -1;
		return 0;
		//return (int) (suspiciousDistance-o.suspiciousDistance);
	}

	public float getVariance() {
		return variance;
	}

	public void setVariance(float variance) {
		this.variance = variance;
	}

	public float getDistanceFromFailedTestCases() {
		return distanceFromFailedTestCases;
	}

	public void setDistanceFromFailedTestCases(float distanceFromFailedTestCases) {
		this.distanceFromFailedTestCases = distanceFromFailedTestCases;
	}

	public float getSuspiciousDistance() {
		return suspiciousDistance;
	}

	public void setSuspiciousDistance(float suspiciousDistance) {
		this.suspiciousDistance = suspiciousDistance;
	}
	
	

}
