package com.yeungeek.util;

/**
 * @ClassName: ScaleStat
 * @Description: TODO
 * @author Anson.Yang
 * @date 2013-1-8 下午03:39:52
 */
public class ScaleStat {

	public int id;
	public String testNo;
	public int patientId;
	public String patientName;
	public int scaleNum;
	public int questionAmount;
	public long updateTime;

	public ScaleStat() {

	}

	public ScaleStat(String testNo) {
		this.testNo = testNo;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the testNo
	 */
	public String getTestNo() {
		return testNo;
	}

	/**
	 * @param testNo
	 *            the testNo to set
	 */
	public void setTestNo(String testNo) {
		this.testNo = testNo;
	}

	/**
	 * @return the patientId
	 */
	public int getPatientId() {
		return patientId;
	}

	/**
	 * @param patientId
	 *            the patientId to set
	 */
	public void setPatientId(int patientId) {
		this.patientId = patientId;
	}

	/**
	 * @return the patientName
	 */
	public String getPatientName() {
		return patientName;
	}

	/**
	 * @param patientName
	 *            the patientName to set
	 */
	public void setPatientName(String patientName) {
		this.patientName = patientName;
	}

	/**
	 * @return the scaleNum
	 */
	public int getScaleNum() {
		return scaleNum;
	}

	/**
	 * @param scaleNum
	 *            the scaleNum to set
	 */
	public void setScaleNum(int scaleNum) {
		this.scaleNum = scaleNum;
	}

	/**
	 * @return the questionAmount
	 */
	public int getQuestionAmount() {
		return questionAmount;
	}

	/**
	 * @param questionAmount
	 *            the questionAmount to set
	 */
	public void setQuestionAmount(int questionAmount) {
		this.questionAmount = questionAmount;
	}

	/**
	 * @return the updateTime
	 */
	public long getUpdateTime() {
		return updateTime;
	}

	/**
	 * @param updateTime
	 *            the updateTime to set
	 */
	public void setUpdateTime(long updateTime) {
		this.updateTime = updateTime;
	}

	@Override
	public String toString() {
		return "ScaleStat [id=" + id + ", testNo=" + testNo + ", patientId="
				+ patientId + ", patientName=" + patientName + ", scaleNum="
				+ scaleNum + ", questionAmount=" + questionAmount
				+ ", updateTime=" + updateTime + "]";
	}

	@Override
	public boolean equals(Object o) {
//		if (o == this) {
//			return true;
//		}

		if (!(o instanceof ScaleStat)) {
			return false;
		}

		ScaleStat stat = (ScaleStat) o;

		return stat.id == id && stat.testNo.equals(this.testNo)
				&& stat.patientId == this.patientId
				&& stat.patientName.equals(this.patientName)
				&& stat.questionAmount != this.questionAmount
				&& stat.scaleNum != this.scaleNum
				&& stat.updateTime == this.updateTime;
	}

	@Override
	public int hashCode() {
		int result = 17;
		result = result * 31 + id;
		result = result * 31 + testNo.hashCode();
		result = result * 31 + patientId;
		result = result * 31 + patientName.hashCode();
		result = result * 31 + scaleNum;
		result = result * 31 + questionAmount;
		result = result * 31 + (int) (updateTime ^ (updateTime >>> 32));
		return result;
	}
}
