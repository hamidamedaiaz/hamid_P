/**
 * Système de logging professionnel pour l'application
 * Permet de tracer les événements, erreurs et actions utilisateur
 */

const LogLevel = {
  DEBUG: 'DEBUG',
  INFO: 'INFO',
  WARN: 'WARN',
  ERROR: 'ERROR',
};

class Logger {
  constructor(context = 'App') {
    this.context = context;
    this.isDevelopment = import.meta.env.DEV;
  }

  _formatMessage(level, message, data = null) {
    const timestamp = new Date().toISOString();
    const prefix = `[${timestamp}] [${level}] [${this.context}]`;
    
    if (data) {
      return `${prefix} ${message}`, data;
    }
    return `${prefix} ${message}`;
  }

  _shouldLog(level) {
    if (!this.isDevelopment && level === LogLevel.DEBUG) {
      return false;
    }
    return true;
  }

  debug(message, data = null) {
    if (!this._shouldLog(LogLevel.DEBUG)) return;
    const formatted = this._formatMessage(LogLevel.DEBUG, message, data);
    console.log(formatted, data || '');
  }

  info(message, data = null) {
    if (!this._shouldLog(LogLevel.INFO)) return;
    const formatted = this._formatMessage(LogLevel.INFO, message, data);
    console.info(formatted, data || '');
  }

  warn(message, data = null) {
    if (!this._shouldLog(LogLevel.WARN)) return;
    const formatted = this._formatMessage(LogLevel.WARN, message, data);
    console.warn(formatted, data || '');
  }

  error(message, error = null) {
    if (!this._shouldLog(LogLevel.ERROR)) return;
    const formatted = this._formatMessage(LogLevel.ERROR, message);
    console.error(formatted, error || '');
    
    // En production, envoyer à un service de monitoring (Sentry, LogRocket, etc.)
    if (!this.isDevelopment && error) {
      this._sendToMonitoring(message, error);
    }
  }

  _sendToMonitoring(message, error) {
    // TODO: Intégration avec service de monitoring
    // Example: Sentry.captureException(error, { extra: { message } });
  }

  // Méthodes spécialisées pour l'UI
  userAction(action, details = null) {
    this.info(`User Action: ${action}`, details);
  }

  apiCall(method, endpoint, status = null) {
    if (status) {
      this.debug(`API ${method} ${endpoint} → ${status}`);
    } else {
      this.debug(`API ${method} ${endpoint}`);
    }
  }

  apiError(method, endpoint, error) {
    this.error(`API ${method} ${endpoint} failed`, error);
  }
}

// Export d'instances pré-configurées pour différents modules
export const appLogger = new Logger('App');
export const apiLogger = new Logger('API');
export const uiLogger = new Logger('UI');

export default Logger;

