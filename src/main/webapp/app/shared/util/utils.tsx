import React from 'react';
export const Spinner = () => {
  return (
    <div
      className="ds-u-display--flex ds-u-justify-content--center ds-u-align-items--center"
      style={{ position: 'absolute', width: '100%', height: '100%', top: '0', left: '0' }}
    >
      <span
        className="ds-c-spinner ds-c-spinner--filled ds-u-fill--background-inverse ds-u-color--base-inverse"
        aria-valuetext="Loading"
        role="progressbar"
      />
    </div>
  );
};
