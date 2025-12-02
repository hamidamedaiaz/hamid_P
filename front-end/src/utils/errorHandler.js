import { apiLogger } from './logger';
import notificationService from './notificationService.jsx';

/**
 * Gestionnaire d'erreurs centralisé pour l'application
 * Gère les erreurs de manière cohérente et user-friendly
 */

export class AppError extends Error {
  constructor(message, type = 'ERROR', userMessage = null, metadata = {}) {
    super(message);
    this.name = 'AppError';
    this.type = type;
    this.userMessage = userMessage || message;
    this.metadata = metadata;
    this.timestamp = new Date().toISOString();
  }
}

export const ErrorType = {
  NETWORK: 'NETWORK_ERROR',
  VALIDATION: 'VALIDATION_ERROR',
  AUTHENTICATION: 'AUTHENTICATION_ERROR',
  AUTHORIZATION: 'AUTHORIZATION_ERROR',
  NOT_FOUND: 'NOT_FOUND_ERROR',
  SERVER: 'SERVER_ERROR',
  BUSINESS: 'BUSINESS_ERROR',
  UNKNOWN: 'UNKNOWN_ERROR',
};

class ErrorHandler {
  /**
   * Gère une erreur de manière centralisée
   * @param {Error} error - L'erreur à gérer
   * @param {Object} context - Contexte additionnel pour le logging
   * @param {boolean} showNotification - Afficher une notification à l'utilisateur
   */
  handle(error, context = {}, showNotification = true) {
    const processedError = this._processError(error);
    
    // Log l'erreur
    this._logError(processedError, context);
    
    // Afficher une notification si demandé
    if (showNotification) {
      this._notifyUser(processedError);
    }
    
    return processedError;
  }

  /**
   * Gère les erreurs API de manière spécifique
   */
  handleApiError(error, endpoint, method = 'GET') {
    const context = {
      endpoint,
      method,
      status: error.response?.status,
      data: error.response?.data,
    };

    return this.handle(error, context, true);
  }

  /**
   * Wrapper pour les appels asynchrones avec gestion d'erreur
   */
  async handleAsync(asyncFn, errorContext = {}) {
    try {
      return await asyncFn();
    } catch (error) {
      throw this.handle(error, errorContext, true);
    }
  }

  _processError(error) {
    // Si c'est déjà une AppError, la retourner
    if (error instanceof AppError) {
      return error;
    }

    // Erreurs réseau
    if (error.message === 'Network Error' || !error.response) {
      return new AppError(
        error.message,
        ErrorType.NETWORK,
        'Impossible de se connecter au serveur. Vérifiez votre connexion internet.',
        { originalError: error }
      );
    }

    // Erreurs HTTP
    if (error.response) {
      const { status, data } = error.response;
      
      switch (status) {
        case 400:
          return new AppError(
            data?.message || 'Requête invalide',
            ErrorType.VALIDATION,
            data?.message || 'Les données fournies sont invalides',
            { status, data }
          );
        
        case 401:
          return new AppError(
            'Non authentifié',
            ErrorType.AUTHENTICATION,
            'Votre session a expiré. Veuillez vous reconnecter.',
            { status, data }
          );
        
        case 403:
          return new AppError(
            'Accès refusé',
            ErrorType.AUTHORIZATION,
            "Vous n'avez pas les permissions nécessaires pour cette action.",
            { status, data }
          );
        
        case 404:
          return new AppError(
            'Ressource non trouvée',
            ErrorType.NOT_FOUND,
            'La ressource demandée est introuvable.',
            { status, data }
          );
        
        case 409:
          return new AppError(
            data?.message || 'Conflit',
            ErrorType.BUSINESS,
            data?.message || 'Cette opération ne peut pas être effectuée.',
            { status, data }
          );
        
        case 500:
        case 502:
        case 503:
          return new AppError(
            'Erreur serveur',
            ErrorType.SERVER,
            'Le serveur rencontre des difficultés. Veuillez réessayer dans quelques instants.',
            { status, data }
          );
        
        default:
          return new AppError(
            data?.message || 'Erreur inconnue',
            ErrorType.UNKNOWN,
            data?.message || 'Une erreur inattendue est survenue.',
            { status, data }
          );
      }
    }

    // Erreur JavaScript standard
    return new AppError(
      error.message,
      ErrorType.UNKNOWN,
      'Une erreur inattendue est survenue.',
      { originalError: error }
    );
  }

  _logError(error, context) {
    const logData = {
      type: error.type,
      message: error.message,
      userMessage: error.userMessage,
      timestamp: error.timestamp,
      metadata: error.metadata,
      context,
    };

    // Log selon la sévérité
    switch (error.type) {
      case ErrorType.VALIDATION:
        apiLogger.warn('Validation error', logData);
        break;
      case ErrorType.NETWORK:
      case ErrorType.SERVER:
        apiLogger.error('Critical error', logData);
        break;
      default:
        apiLogger.error('Error occurred', logData);
    }
  }

  _notifyUser(error) {
    // Ne pas notifier certaines erreurs
    if (this._shouldSuppressNotification(error)) {
      return;
    }

    // Choisir le type de notification selon le type d'erreur
    switch (error.type) {
      case ErrorType.VALIDATION:
        notificationService.warning(error.userMessage);
        break;
      case ErrorType.AUTHENTICATION:
        notificationService.error(error.userMessage, null, { duration: 6000 });
        break;
      default:
        notificationService.error(error.userMessage);
    }
  }

  _shouldSuppressNotification(error) {
    // Liste des erreurs à ne pas notifier
    const suppressedErrors = [
      // Ajoutez ici les types d'erreurs à ne pas notifier
    ];

    return suppressedErrors.includes(error.type);
  }
}

export const errorHandler = new ErrorHandler();
export default errorHandler;

