package es.ull.iis.simulation.hta.radios.wrappers;

public class TimeCostEvent {
	private Double timeEvent;
	private Boolean checked;

	public TimeCostEvent() {
	}
	
	public TimeCostEvent(Double timeEvent, Boolean checked) {
		this.timeEvent = timeEvent;
		this.checked = checked;
	}

	public TimeCostEvent(Double timeEvent) {
		this.timeEvent = timeEvent;
		this.checked = false;
	}

	public Double getTimeEvent() {
		return timeEvent;
	}

	public void setTimeEvent(Double timeEvent) {
		this.timeEvent = timeEvent;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}
	
	public static TimeCostEvent fromTimeEvent (Double timeEvent) {
		return new TimeCostEvent(timeEvent);
	}
}
