"use client";

import { useState, useCallback, useEffect } from "react";

export default function ReadAloud({ targetId = "main-content" }: { targetId?: string }) {
  const [speaking, setSpeaking] = useState(false);

  const stop = useCallback(() => {
    window.speechSynthesis.cancel();
    setSpeaking(false);
  }, []);

  // clean up if the component unmounts while speaking
  useEffect(() => {
    return () => { window.speechSynthesis.cancel(); };
  }, []);

  const toggle = () => {
    if (speaking) {
      stop();
      return;
    }

    const el = document.getElementById(targetId);
    if (!el) return;

    // grab text content, strip extra whitespace
    const text = el.innerText.replace(/\s+/g, " ").trim();
    if (!text) return;

    const utterance = new SpeechSynthesisUtterance(text);
    utterance.rate = 0.95;
    utterance.onend = () => setSpeaking(false);
    utterance.onerror = () => setSpeaking(false);

    window.speechSynthesis.speak(utterance);
    setSpeaking(true);
  };

  return (
    <button
      onClick={toggle}
      aria-label={speaking ? "Stop reading aloud" : "Read page aloud"}
      title={speaking ? "Stop reading" : "Read page aloud"}
      className="read-aloud-btn"
    >
      {speaking ? "Stop Reading" : "Read Aloud"}
    </button>
  );
}
