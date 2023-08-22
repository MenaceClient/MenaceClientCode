package dev.menace.event.events;

public class EventStep extends Event {

    StepState state;
    float stepHeight;

    public EventStep(StepState state, float stepHeight) {
        this.state = state;
        this.stepHeight = stepHeight;
    }

    public StepState getState() {
        return state;
    }

    public float getStepHeight() {
        return stepHeight;
    }

    public void setStepHeight(float stepHeight) {
        this.stepHeight = stepHeight;
    }

    public enum StepState {
        PRE, POST
    }

}
