import { useRef, useState } from "react";
import { FaCloudUploadAlt, FaTimes, FaPlay } from "react-icons/fa";
import {
  createAttachmentFromFile,
  releaseAttachmentPreview,
  ATTACHMENT_TYPE,
  ALLOWED_IMAGE_TYPES,
  ALLOWED_VIDEO_TYPES,
  MAX_ATTACHMENT_SIZE_MB,
} from "../../utils/mockComplaints";
import "./ComplaintAttachmentPicker.css";

const ACCEPT = [...ALLOWED_IMAGE_TYPES, ...ALLOWED_VIDEO_TYPES].join(",");

// Controlled component: `items` is an array of { attachment, previewUrl }.
// The parent form owns the array so it can hand `items.map(i => i.attachment)`
// straight to `addComplaint()` on submit.
function ComplaintAttachmentPicker({ items, onChange }) {
  const inputRef = useRef(null);
  const [errors, setErrors] = useState([]);

  const handleFiles = (fileList) => {
    const files = Array.from(fileList || []);
    if (!files.length) return;

    const nextItems = [...items];
    const nextErrors = [];

    files.forEach((file) => {
      try {
        const { attachment, previewUrl } = createAttachmentFromFile(file);
        nextItems.push({ attachment, previewUrl });
      } catch (err) {
        nextErrors.push(err.message);
      }
    });

    onChange(nextItems);
    setErrors(nextErrors);
  };

  const handleRemove = (attachmentId) => {
    releaseAttachmentPreview(attachmentId);
    onChange(items.filter((it) => it.attachment.id !== attachmentId));
  };

  const handleBrowseClick = () => inputRef.current?.click();

  const handleDrop = (e) => {
    e.preventDefault();
    handleFiles(e.dataTransfer.files);
  };

  return (
    <div className="attachment-picker">
      <div
        className="attachment-dropzone"
        onClick={handleBrowseClick}
        onDragOver={(e) => e.preventDefault()}
        onDrop={handleDrop}
        role="button"
        tabIndex={0}
      >
        <FaCloudUploadAlt className="dropzone-icon" />
        <p className="dropzone-title">Add Photos / Videos</p>
        <p className="dropzone-hint">
          JPG, PNG, WEBP, MP4, MOV, WEBM — up to {MAX_ATTACHMENT_SIZE_MB}MB each
        </p>
        <span className="browse-btn">Browse Files</span>
        <input
          ref={inputRef}
          type="file"
          accept={ACCEPT}
          multiple
          hidden
          onChange={(e) => {
            handleFiles(e.target.files);
            e.target.value = "";
          }}
        />
      </div>

      {errors.length > 0 && (
        <div className="attachment-errors">
          {errors.map((msg, i) => (
            <p key={i}>{msg}</p>
          ))}
        </div>
      )}

      {items.length > 0 && (
        <div className="attachment-grid">
          {items.map(({ attachment, previewUrl }) => (
            <div className="attachment-thumb" key={attachment.id}>
              {attachment.type === ATTACHMENT_TYPE.IMAGE ? (
                <img src={previewUrl} alt={attachment.fileName} />
              ) : (
                <div className="video-thumb">
                  <video src={previewUrl} muted />
                  <FaPlay className="play-icon" />
                </div>
              )}
              <button
                type="button"
                className="remove-btn"
                onClick={() => handleRemove(attachment.id)}
                aria-label={`Remove ${attachment.fileName}`}
              >
                <FaTimes />
              </button>
              <p className="attachment-name" title={attachment.fileName}>
                {attachment.fileName}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

export default ComplaintAttachmentPicker;