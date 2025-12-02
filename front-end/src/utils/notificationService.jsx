import toast from 'react-hot-toast';
import { apiLogger } from './logger';

/**
 * Service de notification professionnel utilisant react-hot-toast
 * Fournit des notifications cohérentes dans toute l'application
 */

const defaultOptions = {
  duration: 4000,
  position: 'top-right',
  style: {
    borderRadius: '12px',
    background: '#fff',
    color: '#171717',
    padding: '16px 20px',
    fontSize: '14px',
    fontWeight: '500',
    boxShadow: '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)',
    border: '1px solid #e5e5e5',
  },
};

class NotificationService {
  success(message, options = {}) {
    apiLogger.info('Notification success:', message);
    return toast.success(message, {
      ...defaultOptions,
      ...options,
      iconTheme: {
        primary: '#34c759',
        secondary: '#fff',
      },
      style: {
        ...defaultOptions.style,
        borderLeft: '4px solid #34c759',
      },
    });
  }

  error(message, error = null, options = {}) {
    apiLogger.error('Notification error:', error || message);
    
    // Formater le message d'erreur de manière user-friendly
    const userMessage = this._formatErrorMessage(message, error);
    
    return toast.error(userMessage, {
      ...defaultOptions,
      duration: 6000, // Plus long pour les erreurs
      ...options,
      iconTheme: {
        primary: '#ff3b30',
        secondary: '#fff',
      },
      style: {
        ...defaultOptions.style,
        borderLeft: '4px solid #ff3b30',
      },
    });
  }

  warning(message, options = {}) {
    apiLogger.warn('Notification warning:', message);
    return toast(message, {
      ...defaultOptions,
      ...options,
      icon: '⚠️',
      style: {
        ...defaultOptions.style,
        borderLeft: '4px solid #ff9500',
      },
    });
  }

  info(message, options = {}) {
    apiLogger.info('Notification info:', message);
    return toast(message, {
      ...defaultOptions,
      ...options,
      icon: 'ℹ️',
      style: {
        ...defaultOptions.style,
        borderLeft: '4px solid #0ea5e9',
      },
    });
  }

  loading(message) {
    return toast.loading(message, {
      ...defaultOptions,
      style: {
        ...defaultOptions.style,
        borderLeft: '4px solid #737373',
      },
    });
  }

  promise(promise, messages) {
    return toast.promise(
      promise,
      {
        loading: messages.loading || 'Chargement...',
        success: messages.success || 'Succès !',
        error: messages.error || 'Une erreur est survenue',
      },
      defaultOptions
    );
  }

  dismiss(toastId) {
    if (toastId) {
      toast.dismiss(toastId);
    } else {
      toast.dismiss();
    }
  }

  _formatErrorMessage(message, error) {
    if (!error) return message;

    // Gérer les erreurs réseau
    if (error.message === 'Network Error' || error.message?.includes('fetch')) {
      return 'Erreur de connexion. Veuillez vérifier votre connexion internet.';
    }

    // Gérer les erreurs HTTP
    if (error.response) {
      const status = error.response.status;
      
      switch (status) {
        case 400:
          return error.response.data?.message || 'Requête invalide';
        case 401:
          return 'Session expirée. Veuillez vous reconnecter.';
        case 403:
          return "Vous n'avez pas les permissions nécessaires.";
        case 404:
          return 'Ressource non trouvée';
        case 409:
          return error.response.data?.message || 'Conflit de données';
        case 500:
          return 'Erreur serveur. Veuillez réessayer plus tard.';
        case 503:
          return 'Service temporairement indisponible';
        default:
          return error.response.data?.message || message;
      }
    }

    // Message par défaut
    return error.message || message;
  }

  // Méthodes spécialisées pour l'UX
  confirmAction(message, onConfirm) {
    const toastId = toast(
      (t) => (
        <div className="flex flex-col gap-3">
          <p className="font-medium text-neutral-900">{message}</p>
          <div className="flex gap-2">
            <button
              onClick={() => {
                onConfirm();
                toast.dismiss(t.id);
              }}
              className="flex-1 px-4 py-2 bg-danger text-white rounded-apple font-semibold
                         hover:bg-danger-dark transition-all duration-200 active:scale-95
                         shadow-apple"
            >
              Confirmer
            </button>
            <button
              onClick={() => toast.dismiss(t.id)}
              className="flex-1 px-4 py-2 bg-neutral-100 text-neutral-900 rounded-apple font-semibold
                         hover:bg-neutral-200 transition-all duration-200 active:scale-95"
            >
              Annuler
            </button>
          </div>
        </div>
      ),
      {
        duration: Infinity,
        style: {
          ...defaultOptions.style,
          minWidth: '320px',
          padding: '20px',
        },
      }
    );
    return toastId;
  }
}

export const notificationService = new NotificationService();
export default notificationService;

