import { useState } from "react";
import { FaFileImage, FaFileVideo, FaTimes, FaExpand } from "react-icons/fa";
import { getAttachmentPreview, ATTACHMENT_TYPE } from "../../utils/mockComplaints";
import "./AttachmentGallery.css";

function formatSize(bytes) {
  if (!bytes && bytes !== 0) return "";
  if (bytes < 1024) return `${bytes} B`;
  if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
  return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
}

// Read-only viewer for an attachment array. Shows the real image/video when
// a live preview exists for this session (see mockComplaints.js), otherwise
// falls back to a file-type card — this is the "reference" the real
// attachment API will later replace.
function AttachmentGallery({ attachments = [] }) {
  const [lightbox, setLightbox] = useState(null); // attachment or null

  if (!attachments.length) {
    return <p className="attachment-gallery-empty">No attachments were added to this complaint.</p>;
  }

  return (
    <>
      <div className="attachment-gallery">
        {attachments.map((att) => {
          const preview = getAttachmentPreview(att.id);
          const isImage = att.type === ATTACHMENT_TYPE.IMAGE;

          return (
            <div
              className="gallery-item"
              key={att.id}
              onClick={() => preview && setLightbox(att)}
              role={preview ? "button" : undefined}
              tabIndex={preview ? 0 : undefined}
            >
              {preview ? (
                <>
                  {isImage ? (
                    <img src={preview} alt={att.fileName} />
                  ) : (
                    <video src={preview} muted />
                  )}
                  <span className="gallery-expand">
                    <FaExpand />
                  </span>
                </>
              ) : (
                <div className="gallery-placeholder">
                  {isImage ? <FaFileImage /> : <FaFileVideo />}
                </div>
              )}
              <div className="gallery-caption">
                <p className="gallery-name" title={att.fileName}>
                  {att.fileName}
                </p>
                <p className="gallery-meta">{formatSize(att.fileSize)}</p>
              </div>
            </div>
          );
        })}
      </div>

      {lightbox && (
        <div className="gallery-lightbox" onClick={() => setLightbox(null)}>
          <button className="gallery-lightbox-close" onClick={() => setLightbox(null)}>
            <FaTimes />
          </button>
          <div className="gallery-lightbox-content" onClick={(e) => e.stopPropagation()}>
            {lightbox.type === ATTACHMENT_TYPE.IMAGE ? (
              <img src={getAttachmentPreview(lightbox.id)} alt={lightbox.fileName} />
            ) : (
              <video src={getAttachmentPreview(lightbox.id)} controls autoPlay />
            )}
            <p>{lightbox.fileName}</p>
          </div>
        </div>
      )}
    </>
  );
}

export default AttachmentGallery;