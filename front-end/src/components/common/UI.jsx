import React from 'react';
import PropTypes from 'prop-types';

/**
 * Composants UI réutilisables avec style Apple-like
 */

// ===== BOUTONS =====

export const Button = ({
  children,
  variant = 'primary',
  size = 'medium',
  disabled = false,
  loading = false,
  onClick,
  className = '',
  ...props
}) => {
  const baseClasses = 'inline-flex items-center justify-center gap-2 font-semibold rounded-apple transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-offset-2 active:scale-95 disabled:opacity-50 disabled:cursor-not-allowed';

  const variantClasses = {
    primary: 'bg-primary-500 text-white hover:bg-primary-600 focus:ring-primary-500 shadow-apple hover:shadow-apple-lg',
    secondary: 'bg-neutral-100 text-neutral-900 hover:bg-neutral-200 focus:ring-neutral-400',
    success: 'bg-success text-white hover:bg-success-dark focus:ring-success shadow-apple hover:shadow-apple-lg',
    danger: 'bg-danger text-white hover:bg-danger-dark focus:ring-danger shadow-apple hover:shadow-apple-lg',
    outline: 'border-2 border-neutral-300 text-neutral-700 hover:bg-neutral-50 focus:ring-neutral-400',
  };

  const sizeClasses = {
    small: 'px-3 py-1.5 text-sm',
    medium: 'px-4 py-2 text-sm',
    large: 'px-6 py-3 text-base',
  };

  return (
    <button
      className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}
      disabled={disabled || loading}
      onClick={onClick}
      {...props}
    >
      {loading && (
        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
      )}
      {children}
    </button>
  );
};

Button.propTypes = {
  children: PropTypes.node.isRequired,
  variant: PropTypes.oneOf(['primary', 'secondary', 'success', 'danger', 'outline']),
  size: PropTypes.oneOf(['small', 'medium', 'large']),
  disabled: PropTypes.bool,
  loading: PropTypes.bool,
  onClick: PropTypes.func,
  className: PropTypes.string,
};

// ===== BADGES =====

export const Badge = ({
  children,
  variant = 'neutral',
  size = 'medium',
  className = '',
}) => {
  const baseClasses = 'inline-flex items-center gap-1 font-semibold rounded-full';

  const variantClasses = {
    primary: 'bg-primary-50 text-primary-700',
    success: 'bg-success-light text-success-dark',
    danger: 'bg-danger-light text-danger-dark',
    warning: 'bg-warning-light text-warning-dark',
    neutral: 'bg-neutral-100 text-neutral-700',
  };

  const sizeClasses = {
    small: 'px-2 py-0.5 text-xs',
    medium: 'px-3 py-1 text-xs',
    large: 'px-4 py-1.5 text-sm',
  };

  return (
    <span className={`${baseClasses} ${variantClasses[variant]} ${sizeClasses[size]} ${className}`}>
      {children}
    </span>
  );
};

Badge.propTypes = {
  children: PropTypes.node.isRequired,
  variant: PropTypes.oneOf(['primary', 'success', 'danger', 'warning', 'neutral']),
  size: PropTypes.oneOf(['small', 'medium', 'large']),
  className: PropTypes.string,
};

// ===== CARTES =====

export const Card = ({
  children,
  hover = false,
  padding = 'medium',
  className = '',
}) => {
  const baseClasses = 'bg-white rounded-apple-lg border border-neutral-200 shadow-apple transition-all duration-200';
  const hoverClasses = hover ? 'hover:shadow-apple-lg hover:-translate-y-1' : '';

  const paddingClasses = {
    none: '',
    small: 'p-4',
    medium: 'p-6',
    large: 'p-8',
  };

  return (
    <div className={`${baseClasses} ${hoverClasses} ${paddingClasses[padding]} ${className}`}>
      {children}
    </div>
  );
};

Card.propTypes = {
  children: PropTypes.node.isRequired,
  hover: PropTypes.bool,
  padding: PropTypes.oneOf(['none', 'small', 'medium', 'large']),
  className: PropTypes.string,
};

// ===== INPUT =====

export const Input = ({
  label,
  error,
  helperText,
  className = '',
  ...props
}) => {
  return (
    <div className="space-y-1.5">
      {label && (
        <label className="block text-sm font-medium text-neutral-700">
          {label}
        </label>
      )}
      <input
        className={`
          w-full px-4 py-2.5 text-sm
          bg-white border border-neutral-300 rounded-apple
          focus:outline-none focus:ring-2 focus:ring-primary-500 focus:border-transparent
          placeholder:text-neutral-400
          transition-all duration-200
          ${error ? 'border-danger focus:ring-danger' : ''}
          ${className}
        `}
        {...props}
      />
      {error && (
        <p className="text-xs text-danger font-medium">{error}</p>
      )}
      {helperText && !error && (
        <p className="text-xs text-neutral-500">{helperText}</p>
      )}
    </div>
  );
};

Input.propTypes = {
  label: PropTypes.string,
  error: PropTypes.string,
  helperText: PropTypes.string,
  className: PropTypes.string,
};

// ===== DIVIDER =====

export const Divider = ({ className = '', label }) => {
  if (label) {
    return (
      <div className={`relative flex items-center ${className}`}>
        <div className="flex-grow border-t border-neutral-200"></div>
        <span className="flex-shrink mx-4 text-sm text-neutral-500 font-medium">{label}</span>
        <div className="flex-grow border-t border-neutral-200"></div>
      </div>
    );
  }

  return <hr className={`border-neutral-200 ${className}`} />;
};

Divider.propTypes = {
  className: PropTypes.string,
  label: PropTypes.string,
};

// ===== ALERT =====

export const Alert = ({
  children,
  variant = 'info',
  dismissible = false,
  onDismiss,
  className = '',
}) => {
  const variantClasses = {
    info: 'bg-info-light border-info text-info-dark',
    success: 'bg-success-light border-success text-success-dark',
    warning: 'bg-warning-light border-warning text-warning-dark',
    danger: 'bg-danger-light border-danger text-danger-dark',
  };

  return (
    <div className={`flex items-start gap-3 p-4 rounded-apple border-l-4 ${variantClasses[variant]} ${className}`}>
      <div className="flex-1 text-sm font-medium">
        {children}
      </div>
      {dismissible && (
        <button
          onClick={onDismiss}
          className="flex-shrink-0 text-current hover:opacity-70 transition-opacity"
        >
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      )}
    </div>
  );
};

Alert.propTypes = {
  children: PropTypes.node.isRequired,
  variant: PropTypes.oneOf(['info', 'success', 'warning', 'danger']),
  dismissible: PropTypes.bool,
  onDismiss: PropTypes.func,
  className: PropTypes.string,
};

