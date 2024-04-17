import { useRef, useState } from 'react';

const useInterval = (callback, delay) => {
  const savedCallback = useRef(callback);

  // State to store the interval ID
  const [intervalId, setIntervalId] = useState(null);
  
  const startInterval = () => {
    console.log('startInterval');
    const id = setInterval(savedCallback.current, delay);
    setIntervalId(id);
  }


  const stopInterval = () => {
    console.log('inside STOP interval');
    clearInterval(intervalId);
    setIntervalId(null);
  }

  return {startInterval, stopInterval};
};

export default useInterval;