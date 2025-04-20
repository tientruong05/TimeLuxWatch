<template>
  <div class="countdown-timer d-flex">
    <div class="time-box">
      <span>{{ timeParts.days }}</span>
      <small class="d-block">ngày</small>
    </div>
    <div class="time-box">
      <span>{{ timeParts.hours }}</span>
      <small class="d-block">giờ</small>
    </div>
    <div class="time-box">
      <span>{{ timeParts.minutes }}</span>
      <small class="d-block">phút</small>
    </div>
    <div class="time-box" style="margin-right: 0">
      <span>{{ timeParts.seconds }}</span>
      <small class="d-block">giây</small>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed, watch } from "vue";

const props = defineProps({
  endTime: {
    type: String,
    required: true,
  },
});

const now = ref(new Date().getTime());
const intervalId = ref(null);

const timeParts = computed(() => {
  const end = new Date(props.endTime).getTime();
  const distance = end - now.value;

  if (distance < 0) {
    return { days: "00", hours: "00", minutes: "00", seconds: "00" };
  }

  const days = Math.floor(distance / (1000 * 60 * 60 * 24));
  const hours = Math.floor(
    (distance % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60)
  );
  const minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
  const seconds = Math.floor((distance % (1000 * 60)) / 1000);

  return {
    days: days.toString().padStart(2, "0"),
    hours: hours.toString().padStart(2, "0"),
    minutes: minutes.toString().padStart(2, "0"),
    seconds: seconds.toString().padStart(2, "0"),
  };
});

const updateTimer = () => {
  now.value = new Date().getTime();
};

const startTimer = () => {
  stopTimer(); // Ensure no duplicate timers
  updateTimer(); // Initial update
  intervalId.value = setInterval(updateTimer, 1000);
};

const stopTimer = () => {
  if (intervalId.value) {
    clearInterval(intervalId.value);
    intervalId.value = null;
  }
};

// Start timer when component mounts or endTime changes
onMounted(startTimer);
watch(() => props.endTime, startTimer); // Restart timer if endTime changes

// Clear interval when component unmounts
onUnmounted(stopTimer);
</script>

<style scoped>
.time-box {
  font-weight: bold;
  background: linear-gradient(
    145deg,
    #333333,
    #222222
  ); /* Dark background for contrast */
  color: white;
  border-radius: 8px;
  margin-right: 8px;
  min-width: 45px;
  padding: 8px 10px; /* Slightly adjusted padding */
  text-align: center;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
}

.time-box:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.4);
}

.time-box span {
  font-size: 1.1rem; /* Slightly adjusted size */
  display: block;
  line-height: 1.2;
  color: #ffffff;
}

.time-box small {
  font-size: 0.65rem; /* Slightly adjusted size */
  text-transform: uppercase;
  color: #f8f8f8;
  letter-spacing: 0.5px;
  opacity: 0.9;
}

@media (max-width: 576px) {
  .time-box {
    min-width: 35px;
    padding: 5px 8px;
    margin-right: 5px;
  }
  .time-box span {
    font-size: 1rem;
  }
  .time-box small {
    font-size: 0.6rem;
  }
}
</style>
